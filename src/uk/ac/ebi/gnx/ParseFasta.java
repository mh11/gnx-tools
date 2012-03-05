/**
 * File: ParseFasta.java
 * Created by: mhaimel
 * Created on: Feb 23, 2012
 * CVS:  $Id: ParseFasta.java 1.0 Feb 23, 2012 2:29:04 PM mhaimel Exp $
 */
package uk.ac.ebi.gnx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mhaimel
 *
 */
public class ParseFasta {
	private volatile InputStream in = null;
	
	private volatile List<Integer> len = new ArrayList<Integer>();
	private volatile long nsCnt = 0;
	private volatile long totalCnt = 0;

	private long minLen = 0;

	public ParseFasta() {
		// empty
	}
	public long getNsCnt() {
		return nsCnt;
	}	
	public void setNsCnt(long nsCnt) {
		this.nsCnt = nsCnt;
	}
	public long getTotalCnt() {
		return totalCnt;
	}
	public void setTotalCnt(long totalCnt) {
		this.totalCnt = totalCnt;
	}
	public void setMinLen(long minLen) {
		this.minLen = minLen;
	}
	public InputStream getIn() {
		return in;
	}
	public void setIn(InputStream in) {
		this.in = in;
	}
	public void setLen(List<Integer> len) {
		this.len = len;
	}
	public List<Integer> getLen() {
		return len;
	}
	public long getMinLen() {
		return minLen;
	}

	public void process() throws IOException {
		int currSeqCnt = 0;
		int currNsCnt = 0;
		int currSeqOfOddChars = 0;
		while(true){
			switch (in.read()) {
			case -1:
				// EOF
			case '>':
				if(currSeqCnt > 0){
					if(addLength(currSeqCnt)){
						this.nsCnt += currNsCnt;
					}
				}
				currSeqCnt = 0;
				currSeqOfOddChars = 0;
				currNsCnt = 0;
				boolean search = true; // ugly, but should work for the moment;
				while(search){
					switch (in.read()) {
					case -1:
						// EOF
						return;
					case '\n':
						search = false;
						break;
					default:
						break;
					}
				}
				break;
			case '\r':
			case '\n':
				// ignore
				break;
				
			case 'n':
			case 'N':
				++currNsCnt;
			case 'a':
			case 'A':
			case 't':
			case 'T':
			case 'g':
			case 'G':
			case 'c':
			case 'C':
				++ currSeqCnt;
				break;
			default:
				++currSeqOfOddChars;
				break;
			}		
		}
	}

	private boolean addLength(int seqLen) {
		if(seqLen >= this.minLen ){
			this.totalCnt += seqLen;
			this.len.add(seqLen);
			return true;
		}
		return false;
	}

	public void reset() {
		this.nsCnt = 0;
		this.totalCnt = 0;
		this.len.clear();
		this.in = null;
	}
}
