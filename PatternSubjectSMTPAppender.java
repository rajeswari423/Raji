package com.ptasocial.log4j;

import javax.mail.internet.MimeUtility;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class extends the standard SMTP appender to be able to add content from the
 * logging event to the email subject.
 * 
 * @author ldomaniczky
 * @see http://lajosd.blogspot.co.uk/2009/09/log4j-smtpappender-exception-info-in.html
 * 
 * NOTE! This needs to be deployed by exporting to a jar /local-jars/log4j-extensions-1.0.jar
 * 1. $ play precompile
 * 2. $ cd <project_root>/precompiled/java
 * 3. $ jar cvf log4j-extensions-1.0.jar com
 * 4. $ mv log4j-extensions-1.0.jar ../local-jars
 */
public class PatternSubjectSMTPAppender extends SMTPAppender {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.net.SMTPAppender#formatBody()
	 */
	protected String formatBody() {
		// Note: this code already owns the monitor for this
		// appender. This frees us from needing to synchronize on 'cb'.
		String sbuf = "";
		String t = layout.getHeader();
		if (t != null)
			sbuf += t;
		int len = cb.length();
		for (int i = 0; i < len; i++) {
			// sbuf.append(MimeUtility.encodeText(layout.format(cb.get())));
			LoggingEvent event = cb.get();
			
			/*********************************************
			 * Start customisation: Setting the subject  *
			 *********************************************/
			if (i == 0) {
				Layout subjectLayout = new EnhancedPatternLayout(getSubject());
				try {
					msg.setSubject(MimeUtility.encodeText(subjectLayout.format(event), "UTF-8", null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			/*********************************************
			 * End customisation                         *
			 *********************************************/
			
			sbuf += layout.format(event);
			if (layout.ignoresThrowable()) {
				String[] s = event.getThrowableStrRep();
				if (s != null) {
					for (int j = 0; j < s.length; j++) {
						sbuf += s[j];
						sbuf += Layout.LINE_SEP;
					}
				}
			}
		}
		t = layout.getFooter();
		if (t != null) {
			sbuf += t;
		}

		return sbuf.toString();
	}
}
