package com.yangyang.java.ai.langchain4j.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yangyang.java.ai.langchain4j.DTO.MergeRequest;
import com.yangyang.java.ai.langchain4j.assistant.XiaozhiAgentAssistant;
import com.yangyang.java.ai.langchain4j.bean.ChatForm;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

@Tag(name = "小智")
@RestController
@RequestMapping(value = "/xiaozhi", produces = "text/stream;charset=utf-8")
public class XiaozhiController {

    // 临时分片文件存储根目录
    private final String UPLOAD_DIR = "uploads/";

    // =================== 分片参数 ===================
    private static final int CHUNK_SIZE = 1000;   // 每个分片的最大字符数
    private static final int CHUNK_OVERLAP = 100; // 分片重叠字符数
    private static final int MAX_CONCURRENT_TASKS = 20; // 同时处理的分片数量

    private final Semaphore taskSemaphore = new Semaphore(MAX_CONCURRENT_TASKS);


    @Autowired
    private XiaozhiAgentAssistant xiaozhiAgentAssistant;

    private static final Logger logger = LoggerFactory.getLogger(XiaozhiController.class);

    @Autowired
    private EmbeddingStore embeddingStore;
    @Autowired
    private EmbeddingModel embeddingModel;

    // 使用单例线程池，避免重复创建
    private ThreadPoolExecutor pdfProcessingExecutor;

    // -----大模型交互-----
    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        return xiaozhiAgentAssistant.chat(chatForm.getMemoryId(), chatForm.getMessage());
    }

    // 检查并初始化临时文件夹和线程池
    @PostConstruct
    public void init() {
        try {
            // 获取项目根目录路径
            String projectRoot = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectRoot, UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory at: {}", uploadPath);
            }

            // 初始化线程池
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            pdfProcessingExecutor = new ThreadPoolExecutor(
                    Math.max(2, availableProcessors / 2),  // 核心线程数
                    availableProcessors,                   // 最大线程数
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(50),        // 合理的队列大小
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.CallerRunsPolicy() // 重要的拒绝策略
            );
            pdfProcessingExecutor.allowCoreThreadTimeOut(true);

        } catch (IOException e) {
            logger.error("无法创建上传目录", e);
            throw new RuntimeException("无法初始化上传目录", e);
        }
    }

    // 优雅关闭线程池
    @PreDestroy
    public void shutdown() {
        if (pdfProcessingExecutor != null) {
            pdfProcessingExecutor.shutdown();
            try {
                if (!pdfProcessingExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    pdfProcessingExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                pdfProcessingExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Operation(summary = "文件分片上传到临时目录")
    @PostMapping("/knowledge/upload-chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName,
            @RequestParam("tempDirName") String tempDirName) {

        try {
            // 文件大小验证
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件为空");
            }

            long MAX_CHUNK_SIZE = 50 * 1024 * 1024; // 50MB
            if (file.getSize() > MAX_CHUNK_SIZE) {
                return ResponseEntity.badRequest().body("分片大小超过50MB限制");
            }

            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            Path tempDirPath = Paths.get(projectRoot, UPLOAD_DIR, "knowledge", "temp", tempDirName);
            if (!Files.exists(tempDirPath)) {
                Files.createDirectories(tempDirPath);
            }

            // 保存分片
            String safeChunkName = chunkIndex + ".tmp";
            Path chunkPath = tempDirPath.resolve(safeChunkName);
            file.transferTo(chunkPath.toFile());
            logger.info("分片上传成功: {} [{}/{}]", tempDirName, chunkIndex + 1, totalChunks);

            return ResponseEntity.ok(tempDirName);

        } catch (IOException e) {
            logger.error("分片上传失败, tempDirName={}, chunkIndex={}", tempDirName, chunkIndex, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("分片上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "合并所有分片")
    @PostMapping("/knowledge/merge")
    public ResponseEntity<String> mergeChunks(@RequestBody MergeRequest request) {
        Path tempDirPath = null;
        Path mergedFilePath = null;

        try {
            String safeFileName = Paths.get(request.getFileName()).getFileName().toString();
            if (!safeFileName.matches("^[a-zA-Z0-9._-]+\\.(pdf|txt|md|docx)$")) {
                return ResponseEntity.badRequest().body("无效的文件名或类型");
            }

            // 参数校验
            if (StringUtils.isEmpty(safeFileName)) {
                return ResponseEntity.badRequest().body("文件名不能为空");
            }
            if (StringUtils.isEmpty(request.getTempDirName())) {
                return ResponseEntity.badRequest().body("临时目录名不能为空");
            }

            String projectRoot = System.getProperty("user.dir");
            tempDirPath = Paths.get(projectRoot, UPLOAD_DIR, "knowledge", "temp", request.getTempDirName());
            mergedFilePath = Paths.get(projectRoot, UPLOAD_DIR, "knowledge", "merged", safeFileName);

            // 验证临时目录存在
            if (!Files.exists(tempDirPath) || !Files.isDirectory(tempDirPath)) {
                return ResponseEntity.badRequest().body("临时目录不存在或已被清理");
            }

            // 创建目标目录
            Files.createDirectories(mergedFilePath.getParent());

            // 合并文件
            logger.info("开始合并文件: {}", safeFileName);
            try (FileOutputStream fos = new FileOutputStream(mergedFilePath.toFile());
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                Files.list(tempDirPath)
                        .filter(path -> path.getFileName().toString().endsWith(".tmp"))
                        .sorted(Comparator.comparingInt(path ->
                                Integer.parseInt(path.getFileName().toString().replace(".tmp", ""))))
                        .forEach(path -> {
                            try (InputStream is = Files.newInputStream(path);
                                 BufferedInputStream bis = new BufferedInputStream(is)) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                while ((bytesRead = bis.read(buffer)) != -1) {
                                    bos.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("合并分片失败: " + path, e);
                            }
                        });
            }
            logger.info("文件合并成功: {}", safeFileName);

            // 处理文件内容
            if (safeFileName.toLowerCase().endsWith(".pdf")) {
                processPdfFile(mergedFilePath, safeFileName);
            } else {
                processTextFile(mergedFilePath, safeFileName);
            }

            logger.info("知识库处理完成: {}", safeFileName);

            return ResponseEntity.ok("文件处理完成");

        } catch (Exception e) {
            logger.error("处理文件失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("处理失败: " + e.getMessage());
        } finally {
            // 无论如何都要清理临时目录
            cleanupTempDirectory(tempDirPath, request.getTempDirName());
        }
    }

    // 处理PDF文件
    private void processPdfFile(Path pdfPath, String fileName) {
        logger.info("开始处理PDF文件: {}", fileName);

        int processedPages = 0;
        try (PDDocument pdfDocument = PDDocument.load(pdfPath.toFile())) {
            int totalPages = pdfDocument.getNumberOfPages();
            PDFTextStripper pdfStripper = new PDFTextStripper();

            logger.info("PDF总页数: {}", totalPages);

            // 预先提取所有页面文本
            List<String> pageTexts = new ArrayList<>();
            for (int page = 1; page <= totalPages; page++) {
                pdfStripper.setStartPage(page);
                pdfStripper.setEndPage(page);
                String pageText = pdfStripper.getText(pdfDocument);
                if (StringUtils.isNotBlank(pageText)) {
                    pageTexts.add(pageText);
                }
            }

            // 使用CountDownLatch等待所有任务完成
            CountDownLatch latch = new CountDownLatch(pageTexts.size());

            for (int i = 0; i < pageTexts.size(); i++) {
                final int pageNum = i + 1;
                final String text = pageTexts.get(i);

                pdfProcessingExecutor.submit(() -> {
                    try {
                        Document document = Document.from(text);
                        document.metadata().put("source", fileName);
                        document.metadata().put("page", String.valueOf(pageNum));
                        ingestToVectorDB(document);
                    } catch (Exception e) {
                        logger.error("处理第{}页失败", pageNum, e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 等待所有任务完成，设置超时时间
            if (!latch.await(30, TimeUnit.MINUTES)) {
                logger.warn("PDF处理超时");
            }

            processedPages = pageTexts.size();
            logger.info("PDF处理完成: {} ({}页)", fileName, processedPages);

        } catch (Exception e) {
            logger.error("处理PDF文件失败: {}", fileName, e);
            throw new RuntimeException("PDF处理失败", e);
        }
    }

    // 处理文本文件
    private void processTextFile(Path filePath, String fileName) {
        try {
            Document document = FileSystemDocumentLoader.loadDocument(filePath.toString());
            if (document != null) {
                document.metadata().put("source", fileName);
                ingestToVectorDB(document);
                logger.info("文本文件处理完成: {}", fileName);
            }
        } catch (Exception e) {
            logger.error("处理文本文件失败: {}", fileName, e);
            throw new RuntimeException("文本文件处理失败", e);
        }
    }

    // 清理临时目录（带重试机制）
    private void cleanupTempDirectory(Path tempDirPath, String tempDirName) {
        if (tempDirPath != null && Files.exists(tempDirPath)) {
            int maxRetries = 3;
            for (int retry = 1; retry <= maxRetries; retry++) {
                try {
                    FileUtils.deleteDirectory(tempDirPath.toFile());
                    logger.info("临时目录清理成功: {}", tempDirName);
                    break;
                } catch (IOException e) {
                    if (retry == maxRetries) {
                        logger.warn("临时目录删除失败(重试{}次): {}", maxRetries, tempDirName, e);
                    } else {
                        logger.warn("临时目录删除失败(第{}次重试): {}", retry, tempDirName, e);
                        try {
                            Thread.sleep(1000 * retry); // 重试延迟
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }
    }


    private void ingestToVectorDB(Document document) {
        final int MAX_RETRIES = 3;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                EmbeddingStoreIngestor.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .build()
                        .ingest(document);

                logger.debug("文档嵌入成功");
                return;

            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    logger.error("向量化存储失败(重试{}次)", MAX_RETRIES, e);
                    return;
                }

                long delayMs = 1000L * attempt; // 1秒, 2秒, 3秒
                logger.warn("处理失败，{}ms后重试(第{}次)", delayMs, attempt);

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("重试被中断");
                    return;
                }
            }
        }
    }

    //通用分片方法
    private List<String> splitIntoChunks(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start += (chunkSize - overlap);
        }
        return chunks;
    }

}




/*    @Operation(summary = "上传知识库")
    @PostMapping("/knowledge/upload")
    public ResponseEntity<String> uploadKnowledge(@RequestParam("file") MultipartFile file){
*//*        //使用FileSystemDocumentLoader读取指定目录下的知识库文档
        //并使用默认的文档解析器对文档进行解析
        Document document1 = FileSystemDocumentLoader.loadDocument("src/main/resources/医院信息.md");
        Document document2 = FileSystemDocumentLoader.loadDocument("src/main/resources/科室信息.md");
        Document document3 = FileSystemDocumentLoader.loadDocument("src/main/resources/神经内科.md");
        List<Document> documents = Arrays.asList(document1, document2, document3);
        //文本向量化并存入向量数据库：将每个片段进行向量化，得到一个嵌入向量
        EmbeddingStoreIngestor
                .builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(documents);*//*
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件为空");
            }

            // 将 MultipartFile 转为临时文件或直接读取内容
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // 读取文件内容并解析
            Document document = FileSystemDocumentLoader.loadDocument(tempFile.getAbsolutePath());

            // 存入向量数据库
            EmbeddingStoreIngestor.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .build()
                    .ingest(Collections.singletonList(document));

            // 上传完成后删除临时文件
            tempFile.delete();

            return ResponseEntity.ok("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
        }
    }*/