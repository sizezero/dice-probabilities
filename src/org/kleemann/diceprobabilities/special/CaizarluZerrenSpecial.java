package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ScaleCumulativeDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

class CaizarluZerrenSpecial extends AbstractSpecial {
	public CaizarluZerrenSpecial(Resources r) {
		super(r.getString(R.string.special_caizerlu_zerren_title), r
				.getString(R.string.special_caizerlu_zerren_description));
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);
		return ScaleCumulativeDistribution.caizarluZerren(d);
	}
}
