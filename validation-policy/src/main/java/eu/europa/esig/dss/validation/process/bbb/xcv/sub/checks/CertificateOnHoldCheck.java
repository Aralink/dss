package eu.europa.esig.dss.validation.process.bbb.xcv.sub.checks;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import eu.europa.esig.dss.jaxb.detailedreport.XmlSubXCV;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.process.AdditionalInfo;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.MessageTag;
import eu.europa.esig.dss.validation.reports.wrapper.CertificateWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.RevocationWrapper;
import eu.europa.esig.dss.x509.crl.CRLReasonEnum;
import eu.europa.esig.jaxb.policy.LevelConstraint;

public class CertificateOnHoldCheck extends ChainItem<XmlSubXCV> {

	private final CertificateWrapper certificate;
	private final Date currentTime;

	public CertificateOnHoldCheck(XmlSubXCV result, CertificateWrapper certificate, Date currentTime, LevelConstraint constraint) {
		super(result, constraint);
		this.certificate = certificate;
		this.currentTime = currentTime;
	}

	@Override
	protected boolean process() {
		RevocationWrapper revocationData = certificate.getLatestRevocationData();
		boolean isOnHold = (revocationData != null) && !revocationData.isStatus() && CRLReasonEnum.certificateHold.name().equals(revocationData.getReason());
		if (isOnHold) {
			isOnHold = revocationData.getRevocationDate() != null && currentTime.after(revocationData.getRevocationDate());
		}
		return !isOnHold;
	}

	@Override
	protected String getAdditionalInfo() {
		RevocationWrapper revocationData = certificate.getLatestRevocationData();
		if (revocationData != null && revocationData.getRevocationDate() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(AdditionalInfo.DATE_FORMAT);
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String revocationDateStr = sdf.format(revocationData.getRevocationDate());
			Object[] params = new Object[] { revocationData.getReason(), revocationDateStr };
			return MessageFormat.format(AdditionalInfo.REVOCATION, params);
		}
		return null;
	}

	@Override
	protected MessageTag getMessageTag() {
		return MessageTag.BBB_XCV_ISCOH;
	}

	@Override
	protected MessageTag getErrorMessageTag() {
		return MessageTag.BBB_XCV_ISCOH_ANS;
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.INDETERMINATE;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return SubIndication.TRY_LATER;
	}

}
