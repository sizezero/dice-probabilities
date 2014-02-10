package org.kleemann.diceprobabilities;

import java.util.ArrayList;
import java.util.List;

import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.CritDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.DogslicerDistribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.app.Activity;
import android.content.Context;
import android.util.SparseIntArray;
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

		def = new NormalSpecial();
		special = new ArrayList<Special>();
		special.add(def);
		special.add(new TwoRollsSpecial());
		special.add(new ForceSecondRollSpecial());
		special.add(new CritSpecial(6));
		special.add(new CritSpecial(4));
		special.add(new DogslicerSpecial());
		special.add(new CaizarluZerrenSpecial());

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

	public interface Special {
		public String getTitle();

		public String getDescription();

		public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount);

		public Distribution getDistribution(SparseIntArray sidesToCount);
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
	
	// ///////////////////////////////////////////////

	// TODO spinner crap needs to get cleaned up

	private static abstract class SpecialImp implements Special {
		private String title;
		private String description;

		public SpecialImp(String title, String description) {
			this.title = title;
			this.description = description;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getDescription() {
			return description;
		}

		/**
		 * <p>
		 * This is what you see in the closed spinner
		 */
		@Override
		public String toString() {
			return title;
		}

		@Override
		public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount) {
			ArrayList<String> dice = new ArrayList<String>();
			for (int i = sidesToCount.size() - 1; i >= 0; --i) {
				final int sides = sidesToCount.keyAt(i);
				final int count = sidesToCount.valueAt(i);
				if (count != 0) {
					if (sides == 1) {
						// d1 is really just adding a constant
						if (count < 0) {
							dice.add("- " + Integer.toString(-count));
						} else {
							dice.add(Integer.toString(count));
						}
					} else {
						dice.add(count + "d" + sides);
					}
				}
			}
			return dice;
		}

		@Override
		public Distribution getDistribution(SparseIntArray sidesToCount) {
			Distribution d = ConstantDistribution.ZERO;
			for (int i = sidesToCount.size() - 1; i >= 0; --i) {
				final int sides = sidesToCount.keyAt(i);
				final int count = sidesToCount.valueAt(i);
				if (count != 0) {
					final Distribution allDiceOfOneType;
					if (sides == 1) {
						allDiceOfOneType = new ConstantDistribution(count);
					} else {
						final DieDistribution singleDie = new DieDistribution(
								sides);
						allDiceOfOneType = SumDistribution.multiply(
								singleDie, count);
					}
					d = SumDistribution.add(d, allDiceOfOneType);
				}
			}
			return d;
		}
	}

	private static class NormalSpecial extends SpecialImp {
		public NormalSpecial() {
			super("Normal", "No special changes are made to die rolls");
		}
	}

	private static class TwoRollsSpecial extends SpecialImp {
		public TwoRollsSpecial() {
			super(
					"Bonus Rolls",
					"If the first roll fails, you have a second chance to roll again. Weapon: Glaive, Icy LongSpear");
		}

		@Override
		public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount) {
			ArrayList<String> dice = super.getFormulaDice(sidesToCount);
			dice.add("x2");
			return dice;
		}

		@Override
		public Distribution getDistribution(SparseIntArray sidesToCount) {
			Distribution d = super.getDistribution(sidesToCount);
			// TODO: take the extra roll into account
			return d;
		}
	}

	private static class ForceSecondRollSpecial extends SpecialImp {
		public ForceSecondRollSpecial() {
			super(
					"Forced Roll",
					"If the first roll succeds, you must roll another successful check to make the roll.  Monster: Hermit Crab");
		}

		@Override
		public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount) {
			ArrayList<String> dice = super.getFormulaDice(sidesToCount);
			dice.add("x2");
			return dice;
		}

		@Override
		public Distribution getDistribution(SparseIntArray sidesToCount) {
			Distribution d = super.getDistribution(sidesToCount);
			// TODO: take the extra roll into account
			return d;
		}
	}

	private static class CritSpecial extends SpecialImp {

		private int critSides;

		public CritSpecial(int sides) {
			super("d" + sides + " crit",
					"All maximum rolls on this die count as one higher. Weapons: Heavy Pick, Scyth");
			this.critSides = sides;
		}

		@Override
		public Distribution getDistribution(SparseIntArray sidesToCount) {
			Distribution d = ConstantDistribution.ZERO;
			for (int i = sidesToCount.size() - 1; i >= 0; --i) {
				final int sides = sidesToCount.keyAt(i);
				final int count = sidesToCount.valueAt(i);
				if (count != 0) {
					final Distribution allDiceOfOneType;
					if (sides == 1) {
						allDiceOfOneType = new ConstantDistribution(count);
					} else {
						// replace the normal die with a crit die
						if (sides == critSides) {
							allDiceOfOneType = SumDistribution
									.multiply(new CritDistribution(sides),
											count);

						} else {
							allDiceOfOneType = SumDistribution
									.multiply(new DieDistribution(sides), count);
						}
					}
					d = SumDistribution.add(d, allDiceOfOneType);
				}
			}
			return d;
		}
	}

	private static class DogslicerSpecial extends SpecialImp {
		public DogslicerSpecial() {
			super("Dogslicer", "For six sided dice, ones count as threes");
		}

		@Override
		public Distribution getDistribution(SparseIntArray sidesToCount) {
			Distribution d = ConstantDistribution.ZERO;
			for (int i = sidesToCount.size() - 1; i >= 0; --i) {
				final int sides = sidesToCount.keyAt(i);
				final int count = sidesToCount.valueAt(i);
				if (count != 0) {
					final Distribution allDiceOfOneType;
					if (sides == 1) {
						allDiceOfOneType = new ConstantDistribution(count);
					} else {
						// replace the normal die with a crit die
						if (sides == 6) {
							allDiceOfOneType = SumDistribution
									.multiply(new DogslicerDistribution(),
											count);

						} else {
							allDiceOfOneType = SumDistribution
									.multiply(new DieDistribution(sides), count);
						}
					}
					d = SumDistribution.add(d, allDiceOfOneType);
				}
			}
			return d;
		}
	}

	private static class CaizarluZerrenSpecial extends SpecialImp {
		public CaizarluZerrenSpecial() {
			super(
					"C. Zerren",
					"When you attempt to defeat Caizarlu Zerren, after you make the roll, roll 1d6. On a 1 or 2, start teh check over. Cards Playued on the previous check do not affect the new check.");
		}

		@Override
		public Distribution getDistribution(SparseIntArray sidesToCount) {
			Distribution d = super.getDistribution(sidesToCount);
			// TODO: reroll a third of the time
			return d;
		}
	}

	public class SpecialAdapter extends ArrayAdapter<Special> {
		List<SpecialImp> data = null;

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
