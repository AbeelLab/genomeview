/*
 * Copyright (c) 2007-2010 by The Broad Institute, Inc. and the Massachusetts Institute of Technology.
 * All Rights Reserved.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL), Version 2.1 which
 * is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 *
 * THE SOFTWARE IS PROVIDED "AS IS." THE BROAD AND MIT MAKE NO REPRESENTATIONS OR WARRANTIES OF
 * ANY KIND CONCERNING THE SOFTWARE, EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NONINFRINGEMENT, OR THE ABSENCE OF LATENT
 * OR OTHER DEFECTS, WHETHER OR NOT DISCOVERABLE.  IN NO EVENT SHALL THE BROAD OR MIT, OR THEIR
 * RESPECTIVE TRUSTEES, DIRECTORS, OFFICERS, EMPLOYEES, AND AFFILIATES BE LIABLE FOR ANY DAMAGES OF
 * ANY KIND, INCLUDING, WITHOUT LIMITATION, INCIDENTAL OR CONSEQUENTIAL DAMAGES, ECONOMIC
 * DAMAGES OR INJURY TO PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER THE BROAD OR MIT SHALL
 * BE ADVISED, SHALL HAVE OTHER REASON TO KNOW, OR IN FACT SHALL KNOW OF THE POSSIBILITY OF THE
 * FOREGOING.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.broad.igv.data;

import org.broad.igv.feature.LocusScore;
import org.broad.igv.track.WindowFunction;

/**
 * @author jrobinso
 */
public class BasicScore implements LocusScore {

    String chr;
    int start;
    int end;
    float score;

    public BasicScore(String chromosome, int start, int end, float score) {
        this.chr = chromosome;
        this.start = start;
        this.end = end;
        this.score = score;
    }

    public BasicScore(BasicScore bs) {
        this.chr = bs.chr;
        this.start = bs.start;
        this.end = bs.end;
        this.score = bs.score;
    }

    public BasicScore copy() {
        return new BasicScore(this);
    }

    public String getChromosome() {
        return chr;
    }

    public String getChr() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getStart() {
        return start;
    }

    public float getScore() {
        return score;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setConfidence(float confidence) {
        // Ignored
    }

    public float getConfidence() {
        return 1.0f;
    }

    public String getValueString(double position, WindowFunction windowFunction) {
        return "Value: " + score;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getExtendedStart() {
        return getStart();
    }

    public int getExtendedEnd() {
        return getEnd();
    }
}
