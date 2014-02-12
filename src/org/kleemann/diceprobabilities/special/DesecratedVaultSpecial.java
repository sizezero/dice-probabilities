package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.CumulativeTransformDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

public class DesecratedVaultSpecial extends AbstractSpecial {

	public DesecratedVaultSpecial(Resources r) {
		super(r.getString(R.string.special_desecrated_vault_title), r
				.getString(R.string.special_desecrated_vault_description));
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);
		return CumulativeTransformDistribution.desecratedVault(d);
	}
}
