/*
package com.yangyang.java.ai.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

@SpringBootTest
public class RAGTest {
 @Test
 public void testReadDocument() {
      //使用FileSystemDocumentLoader读取指定目录下的知识库文档
      //并使用默认的文档解析器TextDocumentParser对文档进行解析
      Document document11 = FileSystemDocumentLoader.loadDocument("C:\\Users\\Wuzhiyuan\\Desktop\\sb.txt");
      System.out.println(document11.text());

*/
/*
      // 加载单个文档
     Document document = FileSystemDocumentLoader.loadDocument("E:/knowledge/file.txt", new TextDocumentParser());

     // 从一个目录中加载所有文档
     List<Document> documents = FileSystemDocumentLoader.loadDocuments("E:/knowledge", new TextDocumentParser());

     // 从一个目录中加载所有的.txt文档
     PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
     List<Document> documents1 = FileSystemDocumentLoader.loadDocuments("E:/knowledge",pathMatcher, new TextDocumentParser());
     for(Document document : documents1){
            System.out.println(document.metadata());
            System.out.println(document.text());
        }


     // 从一个目录及其子目录中加载所有文档
     List<Document> documents2 = FileSystemDocumentLoader.loadDocumentsRecursively("E:/knowledge", new TextDocumentParser());
*//*


    }

    */
/**
     * 解析PDF
     *//*

    @Test
    public void testParsePDF() {
        Document document = FileSystemDocumentLoader.loadDocument(
                "C:\\Users\\Wuzhiyuan\\Desktop\\云计算\\云计算与大数据综合实践-课程课件2024.pdf",
                new ApachePdfBoxDocumentParser());
        System.out.println(document.metadata());
        System.out.println(document.text());
    }
}*/
