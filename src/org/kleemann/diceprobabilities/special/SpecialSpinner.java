package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * <p>
 * Handles all aspects of the spinner object "special". Dice set should query
 * this to determine the currently selected state.
 * 
 * <p>
 * Basic idea of a complex spinner control taken from here:
 * http://adanware.blogspot.in/2012/03/android-custom-spinner-with-custom.html
 */
public class SpecialSpinner {

	private final Spinner spinner;
	private final ArrayList<Special> special;
	private final LayoutInflater layoutInflater;
	private Special selected = null;
	private final Special def;
	private View.OnClickListener changed = null;

	public SpecialSpinner(Activity activity, Spinner spinner) {
		this.spinner = spinner;

		final Resources r = spinner.getResources();
		def = new NormalSpecial(r);
		special = new ArrayList<Special>();
		special.add(def);
		special.add(new SecondChanceSpecial(r));
		special.add(new ForcedRerollSpecial(r));
		special.add(new CritSpecial(r, 6));
		special.add(new CritSpecial(r, 4));
		special.add(new ModifyEachDieSpecial(r, -1));
		special.add(new DogslicerSpecial(r));
		special.add(new FailureSpecial(r
				.getString(R.string.special_one_sixth_failure_title), r
				.getString(R.string.special_one_sixth_failure_description),
				new BigFraction(1, 6)));
		special.add(new FailureSpecial(r
				.getString(R.string.special_two_sixth_failure_title), r
				.getString(R.string.special_two_sixth_failure_description),
				new BigFraction(2, 6)));

		this.layoutInflater = activity.getLayoutInflater();
		SpecialAdapter adapter = new SpecialAdapter(spinner.getContext(),
				R.layout.spinner_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				selected = special.get(pos);
				if (changed != null) {
					changed.onClick(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				selected = null;
				if (changed != null) {
					changed.onClick(null);
				}
			}
		});
	}

	public Special getSelected() {
		return selected == null ? def : selected;
	}

	public int getSelectedItemPosition() {
		return spinner.getSelectedItemPosition();
	}

	public void setSelectedItemPosition(int pos) {
		spinner.setSelection(pos);
	}

	public void setChangeListener(View.OnClickListener changed) {
		this.changed = changed;
	}

	private class SpecialAdapter extends ArrayAdapter<Special> {

		public SpecialAdapter(Context context, int resource) {
			super(context, resource, special);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Ordinary view in Spinner, we use
			// android.R.layout.simple_spinner_item
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View row, ViewGroup parent) {
			// This view starts when we click the spinner.
			if (row == null) {
				row = layoutInflater.inflate(R.layout.special, parent, false);
			}

			Special item = special.get(position);

			if (item != null) { // Parse the data from each object and set it.
				TextView tvTitle = (TextView) row.findViewById(R.id.title);
				if (tvTitle != null) {
					tvTitle.setText(item.getTitle());
				}
				TextView tvDescription = (TextView) row
						.findViewById(R.id.description);
				if (tvDescription != null) {
					tvDescription.setText(item.getDescription());
				}
			}

			return row;
		}
	}
}
