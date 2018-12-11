package edu.sjsu.cmpe202.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.sjsu.cmpe202.SCFParser;

public class SCFParserTest {

	private static String base64EncodedSampleValidSCF = "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";
	private static String base64EncodedSampleInvalidSCF = "AQAEAAAAAQIABAAAAAADAAgQIDBAIFBiTAQAEK3xHzAzzTY3XtovYNwMNu";

	private static Path tempSampleValidSCFFile = null;
	private static Path tempSampleInvalidSCFFile = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		byte[] sampleValidSCFData = Base64.getDecoder().decode(base64EncodedSampleValidSCF);
		tempSampleValidSCFFile = Files.createTempFile("tempSampleValidSCFFile", ".tlv");
		Files.write(tempSampleValidSCFFile, sampleValidSCFData, StandardOpenOption.WRITE);

		byte[] sampleInvalidSCFData = Base64.getDecoder().decode(base64EncodedSampleInvalidSCF);
		tempSampleInvalidSCFFile = Files.createTempFile("tempSampleInvalidSCFFile", ".tlv");
		Files.write(tempSampleInvalidSCFFile, sampleInvalidSCFData, StandardOpenOption.WRITE);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (tempSampleValidSCFFile != null && !tempSampleValidSCFFile.toString().isEmpty()) {
			Files.delete(tempSampleValidSCFFile);
		}

		if (tempSampleInvalidSCFFile != null && !tempSampleInvalidSCFFile.toString().isEmpty()) {
			Files.delete(tempSampleInvalidSCFFile);
		}
	}

	@Test
	public void testMain() {

		try {
			SCFParser.main(new String[] { tempSampleValidSCFFile.toString() });
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testMainWithInvalidFile() {

		try {
			SCFParser.main(new String[] { tempSampleInvalidSCFFile.toString() });
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testMainWithEmptyArguments() {
		SCFParser.main(new String[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMainWithInvalidArguments1() {
		SCFParser.main(new String[] { "" });
	}

	@Test
	public void testReadSCF() throws IOException {

		byte[] sampleSCFData = Base64.getDecoder().decode(base64EncodedSampleValidSCF);

		Path tempSCFFile = Files.createTempFile("tempSCFFile", ".tlv");

		Files.write(tempSCFFile, sampleSCFData, StandardOpenOption.WRITE);

		byte[] scfDataArray = SCFParser.readSCF(tempSCFFile.toString());

		Assert.assertArrayEquals(scfDataArray, sampleSCFData);
	}

	@Test
	public void testParseHeader() {

		try {
			byte[] sampleSCFData = Base64.getDecoder().decode(base64EncodedSampleValidSCF);
			SCFParser.parseHeader(Arrays.copyOfRange(sampleSCFData, 0, 315));
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testParseCertificates() {

		try {
			byte[] sampleSCFData = Base64.getDecoder().decode(base64EncodedSampleValidSCF);
			SCFParser.parseCertificates(Arrays.copyOfRange(sampleSCFData, 316, sampleSCFData.length));
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testParseHeaderWithInvalidInput() {

		try {
			byte[] sampleSCFData = Base64.getDecoder().decode(base64EncodedSampleInvalidSCF);
			SCFParser.parseHeader(Arrays.copyOfRange(sampleSCFData, 0, 315));
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testParseCertificatesWithInvalidInput() {

		try {
			byte[] sampleSCFData = Base64.getDecoder().decode(base64EncodedSampleValidSCF);
			SCFParser.parseCertificates(Arrays.copyOfRange(sampleSCFData, 316, sampleSCFData.length));
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

}
