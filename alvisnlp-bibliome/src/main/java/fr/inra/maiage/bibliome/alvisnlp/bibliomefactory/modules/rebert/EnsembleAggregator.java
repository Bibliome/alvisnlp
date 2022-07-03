package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping;
import fr.inra.maiage.bibliome.util.Pair;

public enum EnsembleAggregator {
	VOTE {
		@Override
		public Pair<Integer,Mapping> aggregate(double[][] ensembleProbas) {
			Mapping explain = new Mapping();
			int[] votes = new int[ensembleProbas[0].length];
			for (double[] probas : ensembleProbas) {
				votes[getHighest(probas)] += 1;
			}
			int mostVotedCat = getHighest(votes);
			double sumProba = 0;
			for (double[] probas : ensembleProbas) {
				sumProba += probas[mostVotedCat];
			}
			double meanProba = sumProba / ensembleProbas.length;
			explain.put("mean-probability", Double.toString(meanProba));
			explain.put("votes", Integer.toString(votes[mostVotedCat]));
			return new Pair<Integer,Mapping>(mostVotedCat, explain);
		}

		private int getHighest(double[] probas) {
			int highest = -1;
			double highestProba = -1;
			for (int nc = 0; nc < probas.length; ++nc) {
				if (probas[nc] > highestProba) {
					highest = nc;
					highestProba = probas[nc];
				}
			}
			return highest;
		}

		private int getHighest(int[] votes) {
			int highest = -1;
			int highestVotes = -1;
			for (int nc = 0; nc < votes.length; ++nc) {
				if (votes[nc] > highestVotes) {
					highest = nc;
					highestVotes = votes[nc];
				}
			}
			return highest;
		}
	};
	
	public abstract Pair<Integer,Mapping> aggregate(double[][] ensembleProbas);
}
