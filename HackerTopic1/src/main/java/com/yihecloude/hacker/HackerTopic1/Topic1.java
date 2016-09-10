package com.yihecloude.hacker.HackerTopic1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class Topic1 {
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws IOException, ParseException {

		Long l = System.currentTimeMillis();

		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
		System.out.println("请输入索引文件路径=");
		Scanner sc = new Scanner(System.in);
		String lineIndex = sc.nextLine();
		Directory directory = FSDirectory.open(new File(lineIndex));
		IndexWriterConfig config = new IndexWriterConfig(
				Version.LUCENE_CURRENT, analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);

		System.out.println("请输入文件路径=");
		String lineStart = sc.nextLine();
		
		String fileName = lineStart;
		
		File file = new File(fileName);

		InputStream in = null;
		byte[] tempByte = new byte[500000];
		int byteread = 0;
		int startL = 0;
		
		// System.out.println("以字节为单位读取文件内容，一次读多个字节：");
		in = new FileInputStream(file);
		while ((byteread = in.read(tempByte)) != -1) {
			String str = new String(tempByte, startL, byteread);
			String[] s = str.split(";");
			for (String string : s) {
				Document doc = new Document();
				doc.add(new Field("contents", UUID.randomUUID().toString(),
						TextField.TYPE_NOT_STORED));
				doc.add(new Field("fileName", string, TextField.TYPE_NOT_STORED));
				iwriter.addDocument(doc);
			}
		}
		in.close();
		iwriter.close();
		System.out.println("build index ok time = " + (System.currentTimeMillis() - l)+" ms");

		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		IndexReader indexReader = DirectoryReader.open(directory);
		Fields fields = MultiFields.getFields(indexReader);
		Iterator<String> fieldsIterator = fields.iterator();
		

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		Set<String> set = map.keySet();

		while (fieldsIterator.hasNext()) {
			String field = fieldsIterator.next();
			Terms terms = fields.terms(field);
			TermsEnum termsEnums = terms.iterator(null);
			BytesRef byteRef = null;
			if (field.equals("fileName")) {
				while ((byteRef = termsEnums.next()) != null) {
					String term = new String(byteRef.bytes, byteRef.offset,
							byteRef.length);

					Query query = new TermQuery(new Term("fileName", term));
					TopDocs rs = isearcher.search(query, null, 5);
					if (map.size() < 5) {
						map.put(term, rs.totalHits);
					} else {
						for (Iterator iterator = set.iterator(); iterator
								.hasNext();) {
							String string = (String) iterator.next();
							if (map.get(string) < rs.totalHits) {
								map.remove(string);
								map.put(term, rs.totalHits);
								break;
							}
						}
					}
				}
			}
		}
		System.out.println("done time = " + (System.currentTimeMillis() - l)+" ms");
		ireader.close();
		directory.close();
		System.out.println("排名前五的词如下：");
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			//System.out.println("最后结果：" + string + ";" + map.get(string));
			System.out.println(string);
		}
	}
}