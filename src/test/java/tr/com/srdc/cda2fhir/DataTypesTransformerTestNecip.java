package tr.com.srdc.cda2fhir;

import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import org.junit.Assert;
import org.junit.Test;
import org.openhealthtools.mdht.uml.hl7.datatypes.CV;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.EN;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVXB_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.TEL;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityNameUse;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
import tr.com.srdc.cda2fhir.impl.DataTypesTransformerImpl;

/**
 * Created by necip on 7/25/2016
 */
public class DataTypesTransformerTestNecip {

    DataTypesTransformer dtt = new DataTypesTransformerImpl();
    
    
    
    @Test
    public void testIVL_TS2Period(){
    	// simple instance test 1
    	
    	IVL_TS ivl_ts = DatatypesFactory.eINSTANCE.createIVL_TS();
    	
    	IVXB_TS ivxb_tsLow = DatatypesFactory.eINSTANCE.createIVXB_TS();
    	IVXB_TS ivxb_tsHigh = DatatypesFactory.eINSTANCE.createIVXB_TS();
    	
    	ivxb_tsLow.setValue("19630116");
    	ivxb_tsHigh.setValue("20151122");
    	
    	ivl_ts.setLow(ivxb_tsLow);
    	ivl_ts.setHigh(ivxb_tsHigh);
    	
    	PeriodDt period = dtt.IVL_TS2Period(ivl_ts);
    	
    	// Notice that Date.getYear() returns THE_YEAR - 1900. It returns 116 for 2016 since 2016-1900 = 116.
    	Assert.assertEquals("IVL_TS.low(year) was not transformed",1963-1900,period.getStart().getYear());
    	// Notice that Date.getMonth() returns THE_MONTH - 1 (since the months are indexed btw the range 0-11)
    	Assert.assertEquals("IVL_TS.low(month) was not transformed",1-1,period.getStart().getMonth());
    	Assert.assertEquals("IVL_TS.low(date[1-31]) was not transformed",16,period.getStart().getDate());
    	
    	Assert.assertEquals("IVL_TS.high(year) was not transformed",2015-1900,period.getEnd().getYear());
    	Assert.assertEquals("IVL_TS.high(month) was not transformed",11-1,period.getEnd().getMonth());
    	Assert.assertEquals("IVL_TS.high(date[1-31]) was not transformed",22,period.getEnd().getDate());
    	
    	
    	// null instance test
    	IVL_TS ivl_ts2 = null;
    	PeriodDt period2 = dtt.IVL_TS2Period(ivl_ts2);
    	Assert.assertNull("IVL_TS null instance transform failed", period2);
    	
    	// nullFlavor instance test
    	IVL_TS ivl_ts3 = DatatypesFactory.eINSTANCE.createIVL_TS();
    	ivl_ts3.setNullFlavor(NullFlavor.NI);
    	PeriodDt period3 = dtt.IVL_TS2Period(ivl_ts3);
    	Assert.assertNull("IVL_TS.nullFlavor set instance transform failed",period3);
    	
    	
    }
    
    @Test
    public void testEN2HumanName(){
    	// simple instance test 1
    	
    	EN en = DatatypesFactory.eINSTANCE.createEN();

    	// Notice that EntityNameUse.P maps to NameUseEnum.NICKNAME.
    	// For further info, visit https://www.hl7.org/fhir/valueset-name-use.html
    	en.getUses().add(EntityNameUse.P);
    	en.addText("theText");
    	en.addFamily("theFamily");
    	en.addGiven("theGiven");
    	en.addPrefix("thePrefix");
    	en.addSuffix("theSuffix");
    	
    	// Data for ivl_ts:  low: 19950127, high: 20160228
    	IVL_TS ivl_ts = DatatypesFactory.eINSTANCE.createIVL_TS("19950115","20160228");
    	en.setValidTime(ivl_ts);
    	
    	HumanNameDt humanName = dtt.EN2HumanName(en);
    	
    	Assert.assertEquals("EN.use was not transformed","nickname",humanName.getUse());
    	Assert.assertEquals("EN.text was not transformed","theText",humanName.getText());
    	Assert.assertEquals("EN.family was not transformed","theFamily",humanName.getFamily().get(0).getValue());
    	Assert.assertEquals("EN.given was not transformed","theGiven",humanName.getGiven().get(0).getValue());
    	Assert.assertEquals("EN.prefix was not transformed","thePrefix",humanName.getPrefix().get(0).getValue());
    	Assert.assertEquals("EN.suffix was not transformed","theSuffix",humanName.getSuffix().get(0).getValue());
    	// EN.period tests for the simple instance test 1
    	
    	PeriodDt en_period = dtt.IVL_TS2Period(ivl_ts);
    	Assert.assertEquals("EN.period(low) was not transformed",en_period.getStart(),humanName.getPeriod().getStart());
    	Assert.assertEquals("EN.period(high) was not transformed",en_period.getEnd(),humanName.getPeriod().getEnd());
    	
    	// null instance test
    	EN en2 = null;
    	HumanNameDt humanName2 = dtt.EN2HumanName(en2);
    	Assert.assertNull("ED null instance transform failed", humanName2);
    	
    	
    	// nullFlavor instance test
    	EN en3 = DatatypesFactory.eINSTANCE.createEN();
    	en3.setNullFlavor(NullFlavor.NI);
    	HumanNameDt humanName3 = dtt.EN2HumanName(en3);
    	Assert.assertNull("EN.nullFlavor set instance transform failed",humanName3);
    }
    
    @Test
    public void testED2Attachment(){
    	// simple instance test
    	
    	// TODO: After the implementation of ed.title.data is completed, Attachment.title will be tested
    	
    	ED ed = DatatypesFactory.eINSTANCE.createED();
    	ed.setMediaType("theMediaType");
    	ed.setLanguage("theLanguage");
    	ed.addText("theData");
    		TEL theTel = DatatypesFactory.eINSTANCE.createTEL();
    		theTel.setValue("theUrl");
    	ed.setReference(theTel);
    	ed.setIntegrityCheck("theIntegrityCheck".getBytes());	
    	
    	
    	AttachmentDt attachment = dtt.ED2Attachment(ed);
    	Assert.assertEquals("ED.mediaType was not transformed","theMediaType",attachment.getContentType());
    	Assert.assertEquals("ED.language was not transformed","theLanguage",attachment.getLanguage());
    	Assert.assertArrayEquals("ED.data was not transformed","theData".getBytes(),attachment.getData());
    	Assert.assertEquals("ED.reference.literal was not transformed","theUrl",attachment.getUrl());
    	Assert.assertArrayEquals("ED.integrityCheck was not transformed","theIntegrityCheck".getBytes(),attachment.getHash());
    	
    	
    	// null instance test
    	ED ed2 = null;
    	AttachmentDt attachment2 = dtt.ED2Attachment(ed2);
    	Assert.assertNull("ED null instance transform failed", attachment2);
    	
    	// nullFlavor instance test
    	ED ed3 = DatatypesFactory.eINSTANCE.createED();
    	ed3.setNullFlavor(NullFlavor.NI);
    	AttachmentDt attachment3 = dtt.ED2Attachment(ed3);
    	Assert.assertNull("ED.nullFlavor set instance transform failed",attachment3);
    	
    }
    
    @Test
    public void testCV2Coding(){
    	// simple instance test
    	CV cv = DatatypesFactory.eINSTANCE.createCV();
    	cv.setCodeSystem("theCodeSystem");
    	cv.setCodeSystemVersion("theCodeSystemVersion");
    	cv.setCode("theCode");
    	cv.setDisplayName("theDisplayName");
    	// TODO: The mapping btw CodingDt.userSelected and CD.codingRationale doesn't exist write to impl
    	
    	CodingDt coding = dtt.CV2Coding(cv);
    	
    	Assert.assertEquals("CV.codeSystem was not transformed","theCodeSystem",coding.getSystem());
    	Assert.assertEquals("CV.codeSystemVersion was not transformed","theCodeSystemVersion",coding.getVersion());
    	Assert.assertEquals("CV.code was not transformed","theCode",coding.getCode());
    	Assert.assertEquals("CV.displayName was not transformed","theDisplayName",coding.getDisplay());
    	
    	
    	// null instance test
    	CV cv2 = null;
    	CodingDt coding2 = dtt.CV2Coding(cv2);
    	Assert.assertNull("CV null instance transform failed", coding2);
    	
    	// nullFlavor instance test
    	CV cv3 = DatatypesFactory.eINSTANCE.createCV();
    	cv3.setNullFlavor(NullFlavor.NI);
    	CodingDt coding3 = dtt.CV2Coding(cv3);
    	Assert.assertNull("CV.nullFlavor set instance transform failed",coding3);
    }
}