package com.ethlo.web.webclient.plugins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.uadetector.UserAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.util.StringUtils;

import com.ethlo.web.filtermapping.UserAgentRequestMatcher;
import com.ethlo.web.filtermapping.VersionNumber;

/**
 * 
 * @author Morten Haraldsen
 */
public abstract class AroundFilterPlugin implements FilterPlugin
{
	protected Logger logger = LoggerFactory.getLogger(AroundFilterPlugin.class);
	private RequestMatcher matcher;
	
	@Override
	public final boolean filterBefore(HttpServletRequest request, HttpServletResponse response)
	{
		if (matcher.matches(request))
		{
			return this.doFilterBefore(request, response);
		}
		else
		{
			logger.debug("No match for filter " + this.getClass().getSimpleName() + " for request " + request.getRequestURI());
		}
		return true;
	}
	
	@Override
	public final void filterAfter(HttpServletRequest request, HttpServletResponse response)
	{
		if (matcher.matches(request))
		{
			this.doFilterAfter(request, response);
		}
		else
		{
			logger.debug("No match for filter " + this.getClass().getSimpleName() + " for request " + request.getRequestURI());
		}
	}
	
	public void setMatcher(RequestMatcher matcher)
	{
		this.matcher = matcher;
	}

	protected abstract boolean doFilterBefore(HttpServletRequest request, HttpServletResponse response);
	
	protected abstract void doFilterAfter(HttpServletRequest request, HttpServletResponse response);

	public UserAgent getUserAgent(HttpServletRequest request)
	{
		return UserAgentRequestMatcher.getUserAgentParser().parse(request.getHeader("User-Agent"));
	}

	protected VersionNumber getHttpVersion(HttpServletRequest request)
	{
		final String protocol = request.getProtocol();
		if (protocol != null)
		{
			final String[] versionNumbers = StringUtils.split(StringUtils.split(protocol, "/")[1], ".");
			return new VersionNumber(Integer.parseInt(versionNumbers[0]), Integer.parseInt(versionNumbers[1]));
		}
		return VersionNumber.UNDETERMINED;
	}
}
