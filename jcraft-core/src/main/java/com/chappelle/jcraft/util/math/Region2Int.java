package com.chappelle.jcraft.util.math;

import java.util.Iterator;

public class Region2Int implements Iterable<Vector2Int>
{
	public static final Region2Int EMPTY = new Region2Int(new Vector2Int(0, 0), new Vector2Int(0, 0));

	private Vector2Int min;
	private Vector2Int size;

	public Region2Int(Vector2Int min, Vector2Int size)
	{
		this.min = min;
		this.size = size;
	}

	public boolean encompasses(Vector2Int v)
	{
		return v.x >= min.x && v.z >= min.z && v.x <= (min.x + size.x) && v.z <= (min.z + size.z);
	}

	public Vector2Int size()
	{
		return new Vector2Int(size);
	}
	
    public static Region2Int createFromCenterExtents(Vector2Int center, int extent) 
    {
        Vector2Int min = new Vector2Int(center.x - extent, center.z - extent);
        Vector2Int max = new Vector2Int(center.x + extent, center.z + extent);
        return createFromMinMax(min, max);
    }

	public Region2Int expand(int amount)
	{
		Vector2Int expandedMin = min.subtract(amount, amount);
		Vector2Int expandedMax = max().add(amount, amount);
		return createFromMinMax(expandedMin, expandedMax);
	}

	public static Region2Int createFromMinMax(Vector2Int min, Vector2Int max)
	{
		Vector2Int size = new Vector2Int(max.x - min.x + 1, max.z - min.z + 1);
		if(size.x <= 0 || size.z <= 0)
		{
			return EMPTY;
		}
		return new Region2Int(min, size);

	}

    public Vector2Int max() 
    {
    	Vector2Int max = new Vector2Int(min);
        max.addLocal(size);
        max.subtractLocal(1, 1);
        return max;
    }

	/**
	 * @param other
	 * @return An iterator over the positions in this region that aren't in
	 *         other
	 */
	public Iterator<Vector2Int> subtract(Region2Int other)
	{
		return new SubtractiveIterator(other);
	}

	@Override
	public Iterator<Vector2Int> iterator()
	{
		return new Region2IntIterator();
	}

	private class Region2IntIterator implements Iterator<Vector2Int>
	{
		Vector2Int pos;

		public Region2IntIterator()
		{
			this.pos = new Vector2Int();
		}

		@Override
		public boolean hasNext()
		{
			return pos.x < size.x;
		}

		@Override
		public Vector2Int next()
		{
			Vector2Int result = new Vector2Int(pos.x + min.x, pos.z + min.z);
			pos.z++;
			if(pos.z >= size.z)
			{
				pos.z = 0;
				pos.x++;
			}
			return result;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Not supported.");
		}
	}

	private class SubtractiveIterator implements Iterator<Vector2Int>
	{
		private Iterator<Vector2Int> innerIterator;
		private Vector2Int next;
		private Region2Int other;

		public SubtractiveIterator(Region2Int other)
		{
			this.other = other;
			innerIterator = iterator();
			updateNext();
		}

		private void updateNext()
		{
			while(innerIterator.hasNext())
			{
				next = innerIterator.next();
				if(!other.encompasses(next))
				{
					return;
				}
			}
			next = null;
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public Vector2Int next()
		{
			Vector2Int result = new Vector2Int(next.x, next.z);
			updateNext();
			return result;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Region2Int other = (Region2Int) obj;
		if(min == null)
		{
			if(other.min != null)
				return false;
		}
		else if(!min.equals(other.min))
			return false;
		if(size == null)
		{
			if(other.size != null)
				return false;
		}
		else if(!size.equals(other.size))
			return false;
		return true;
	}

}