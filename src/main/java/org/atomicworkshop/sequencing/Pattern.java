package org.atomicworkshop.sequencing;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Pattern
{
	private boolean[][] patternData = new boolean[16][25];

	public void setPitchAtInternal(int interval, int pitch)
	{
		if (pitch > 24) {
			pitch %= 25;
		}

		patternData[interval][pitch] = true;
	}

	public void resetPitchAtInterval(int interval, int pitch) {
		if (pitch > 24) {
			pitch %= 25;
		}

		patternData[interval][pitch] = false;
	}

	public Iterable<Byte> getPitchesAtInterval(int interval)
	{
		return () -> new PitchIterator(patternData[interval]);
	}

	private static class PitchIterator implements Iterator<Byte>
	{
		private byte currentPitch;
		private final boolean[] enabledPitches;

		public PitchIterator(boolean[] enabledPitches)
		{
			this.enabledPitches = enabledPitches;
			currentPitch = -1;
		}

		@Override
		public boolean hasNext()
		{
			++currentPitch;
			while (currentPitch < enabledPitches.length) {
				if (enabledPitches[currentPitch]) {
					return true;
				}
			}

			return false;
		}

		@Override
		public Byte next()
		{
			if (currentPitch >= enabledPitches.length) {
				throw new NoSuchElementException();
			}
			return currentPitch;
		}
	}
}
