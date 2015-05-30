package com.chappelle.jcraft.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Profiler
{
	/** List of parent sections */
	private final List<String> sectionList = new ArrayList<>();

	/** List of timestamps (System.nanoTime) */
	private final List<Long> timestampList = new ArrayList<>();

	/** Flag profiling enabled */
	public boolean profilingEnabled;

	/** Current profiling section */
	private String profilingSection = "";

	/** Profiling map */
	private final Map<String, Long> profilingMap = new HashMap<>();

	/**
	 * Clear profiling.
	 */
	public void clearProfiling()
	{
		this.profilingMap.clear();
		this.profilingSection = "";
		this.sectionList.clear();
	}

	/**
	 * Start section
	 */
	public void startSection(String section)
	{
		if (this.profilingEnabled)
		{
			if (this.profilingSection.length() > 0)
			{
				this.profilingSection = this.profilingSection + ".";
			}

			this.profilingSection = this.profilingSection + section;
			this.sectionList.add(this.profilingSection);
			this.timestampList.add(Long.valueOf(System.nanoTime()));
		}
	}

	/**
	 * End section
	 */
	public void endSection()
	{
		if (this.profilingEnabled)
		{
			long currentTime = System.nanoTime();
			long sectionTimestamp = this.timestampList.remove(this.timestampList.size() - 1);
			this.sectionList.remove(this.sectionList.size() - 1);
			long elapsedTime = currentTime - sectionTimestamp;

			if (this.profilingMap.containsKey(this.profilingSection))
			{
				this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + elapsedTime);
			} 
			else
			{
				this.profilingMap.put(this.profilingSection, elapsedTime);
			}

			if (elapsedTime > 100000000L)
			{
				System.out.println("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double) elapsedTime / 1000000.0D + " ms");
			}

			this.profilingSection = !this.sectionList.isEmpty() ? (String) this.sectionList.get(this.sectionList.size() - 1) : "";
		}
	}

	/**
	 * Get profiling data
	 */
	public List<ProfilerResult> getProfilingData(String sectionToRetrieve)
	{
		if (!this.profilingEnabled)
		{
			return Collections.emptyList();
		} 
		else
		{
			long i = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
			long j = this.profilingMap.containsKey(sectionToRetrieve) ? this.profilingMap.get(sectionToRetrieve) : -1L;
			List<ProfilerResult> results = new ArrayList<>();

			if (sectionToRetrieve.length() > 0)
			{
				sectionToRetrieve = sectionToRetrieve + ".";
			}

			long totalTime = 0L;
			Iterator<String> iterator = this.profilingMap.keySet().iterator();

			while (iterator.hasNext())
			{
				String section = iterator.next();

				if (section.length() > sectionToRetrieve.length() && section.startsWith(sectionToRetrieve) && section.indexOf(".", sectionToRetrieve.length() + 1) < 0)
				{
					totalTime += this.profilingMap.get(section);
				}
			}

			float f = (float) totalTime;

			if (totalTime < j)
			{
				totalTime = j;
			}

			if (i < totalTime)
			{
				i = totalTime;
			}

			Iterator<String> profilingKeys = this.profilingMap.keySet().iterator();
			String currentKey;

			while (profilingKeys.hasNext())
			{
				currentKey = profilingKeys.next();

				if (currentKey.length() > sectionToRetrieve.length() && currentKey.startsWith(sectionToRetrieve) && currentKey.indexOf(".", sectionToRetrieve.length() + 1) < 0)
				{
					long l = this.profilingMap.get(currentKey).longValue();
					double d0 = (double) l * 100.0D / (double) totalTime;
					double d1 = (double) l * 100.0D / (double) i;
					String section = currentKey.substring(sectionToRetrieve.length());
					results.add(new ProfilerResult(section, d0, d1));
				}
			}

			profilingKeys = this.profilingMap.keySet().iterator();

			while (profilingKeys.hasNext())
			{
				currentKey = (String) profilingKeys.next();
				this.profilingMap.put(currentKey, this.profilingMap.get(currentKey) * 999L / 1000L);
			}

			if ((float) totalTime > f)
			{
				results.add(new ProfilerResult("unspecified", (double) ((float) totalTime - f) * 100.0D / (double) totalTime, (double) ((float) totalTime - f) * 100.0D / (double) i));
			}

			Collections.sort(results);
			results.add(0, new ProfilerResult(sectionToRetrieve, 100.0D, (double) totalTime * 100.0D / (double) i));
			return results;
		}
	}

	/**
	 * End current section and start a new section
	 */
	public void endStartSection(String section)
	{
		this.endSection();
		this.startSection(section);
	}

	public String getNameOfLastSection()
	{
		return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String) this.sectionList.get(this.sectionList.size() - 1);
	}
}
