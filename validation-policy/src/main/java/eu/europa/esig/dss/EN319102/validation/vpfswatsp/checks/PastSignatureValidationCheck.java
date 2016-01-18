package eu.europa.esig.dss.EN319102.validation.vpfswatsp.checks;

import java.util.Date;

import eu.europa.esig.dss.EN319102.bbb.ChainItem;
import eu.europa.esig.dss.EN319102.policy.ValidationPolicy;
import eu.europa.esig.dss.EN319102.policy.ValidationPolicy.Context;
import eu.europa.esig.dss.EN319102.validation.vpfswatsp.POEExtraction;
import eu.europa.esig.dss.EN319102.validation.vpfswatsp.checks.psv.PastSignatureValidation;
import eu.europa.esig.dss.jaxb.detailedreport.XmlPSV;
import eu.europa.esig.dss.jaxb.detailedreport.XmlValidationProcessArchivalData;
import eu.europa.esig.dss.validation.SignatureWrapper;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.MessageTag;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.report.DiagnosticData;
import eu.europa.esig.jaxb.policy.LevelConstraint;

public class PastSignatureValidationCheck extends ChainItem<XmlValidationProcessArchivalData> {

	private final SignatureWrapper signature;
	private final DiagnosticData diagnosticData;
	private final POEExtraction poe;
	private final Date currentTime;
	private final ValidationPolicy policy;
	private final Context context;

	private Indication indication;
	private SubIndication subIndication;

	public PastSignatureValidationCheck(XmlValidationProcessArchivalData result, SignatureWrapper signature, DiagnosticData diagnosticData, POEExtraction poe,
			Date currentTime, ValidationPolicy policy, Context context, LevelConstraint constraint) {
		super(result, constraint);

		this.signature = signature;
		this.diagnosticData = diagnosticData;
		this.poe = poe;
		this.currentTime = currentTime;
		this.policy = policy;
		this.context = context;
	}

	@Override
	protected boolean process() {
		PastSignatureValidation psv = new PastSignatureValidation(signature, diagnosticData, poe, currentTime, policy, context);
		XmlPSV psvResult = psv.execute();

		if (psvResult != null && psvResult.getConclusion() != null && Indication.VALID.equals(psvResult.getConclusion().getIndication())) {
			return true;
		} else {
			indication = psvResult.getConclusion().getIndication();
			subIndication = psvResult.getConclusion().getSubIndication();
			return false;
		}
	}

	@Override
	protected MessageTag getMessageTag() {
		return MessageTag.PSV_IPSVC;
	}

	@Override
	protected MessageTag getErrorMessageTag() {
		return MessageTag.PSV_IPSVC_ANS;
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return indication;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return subIndication;
	}

}
