package net.malisis.ego.atlas;

import com.google.common.collect.Lists;
import net.malisis.ego.EGO;
import net.malisis.ego.atlas.Atlas.Holder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class Stitcher
{
	private Node root = new Node(0, 0, 256, 256);
	private int width;
	private int height;
	private final int maxWidth;
	private final int maxHeight;

	public Stitcher(int maxWidth, int maxHeight)
	{
		int size = 32;
		root = new Node(0, 0, size, size);
		this.width = size;
		this.height = size;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public int width()
	{
		return this.width;
	}

	public int height()
	{
		return this.height;
	}

	public void stitch(Map<ResourceLocation, Holder> holders)
	{
		ProgressBar bar = ProgressManager.push("Gui Texture stitching", holders.size());
		holders.values()
			   .stream()
			   .sorted()
			   .findFirst()
			   .ifPresent(h -> {
				   root = new Node(0, 0, h.width(), h.height());
				   width = h.width();
				   height = h.height();
			   });

		holders.values()
			   .stream()
			   .sorted()
			   .forEach(holder -> {
				   bar.step(holder.toString());

				   //   EGO.log.info("Processing {}", holder);

				   Node node = findNode(root, holder.width(), holder.height());
				   if (node != null)
					   node.split(holder.width(), holder.height());
				   else
					   node = growNode(root, holder.width(), holder.height());

				   if (node != null)
				   {
					   holder.setPosition(node.x, node.y);
					   //	   EGO.log.info("Added {},{} ({}x{})", node.x, node.y, node.width, node.height);
				   }
				   else
					   EGO.log.error("No place to fit {}.", holder);
			   });

		this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
		net.minecraftforge.fml.common.ProgressManager.pop(bar);
	}

	private Node findNode(Node root, int width, int height)
	{
		//current node already used, check right, then down
		if (root.used())
		{
			Node node = findNode(root.right(), width, height);
			if (node != null)
				return node;
			return findNode(root.down(), width, height);
		}

		//node not used, and texture fits
		if (root.width >= width && root.height >= height)
			return root;

		//no node found
		return null;
	}

	private Node growNode(Node root, int width, int height)
	{
		boolean canGrowDown = width <= root.width;
		boolean canGrowRight = height <= root.height;

		boolean shouldGrowRight = canGrowRight && (root.height >= root.width + width); // attempt to keep square-ish by growing right when
		// height is much greater than width
		boolean shouldGrowDown = canGrowDown && (root.width >= root.height + height); // attempt to keep square-ish by growing down  when
		// width  is much greater than height

		if (shouldGrowRight)
			return growRight(root, width, height);
		else if (shouldGrowDown)
			return growDown(root, width, height);
		else if (canGrowRight)
			return growRight(root, width, height);
		else if (canGrowDown)
			return growDown(root, width, height);
		else
			return null;
	}

	private Node growRight(Node root, int width, int height)
	{
		Node newRoot = new Node(0, 0, root.width + width, root.height);
		newRoot.setNodes(new Node(root.width, 0, width, root.height), root);
		this.root = newRoot;
		this.width = newRoot.width;
		//		EGO.log.info("Atlas new width : " + this.width);

		Node node = findNode(newRoot, width, height);
		if (node != null)
			node.split(width, height);
		return node;
	}

	private Node growDown(Node root, int width, int height)
	{
		Node newRoot = new Node(0, 0, root.width, root.height + height);
		newRoot.setNodes(root, new Node(0, root.height, root.width, height));
		this.root = newRoot;
		this.height = newRoot.height;
		//		EGO.log.info("Atlas new height : " + this.height);

		Node node = findNode(newRoot, width, height);
		if (node != null)
			node.split(width, height);
		return node;
	}

	public static class Node
	{
		public final int x;
		public final int y;
		public final int width;
		public final int height;
		private Node right;
		private Node down;
		private boolean used;

		public Node(int x, int y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public boolean used()
		{
			return used;
		}

		public Node right()
		{
			return right;
		}

		public Node down()
		{
			return down;
		}

		public void split(int w, int h)
		{
			setNodes(new Node(x + w, y, width - w, h), new Node(x, y + h, width, height - h));
		}

		public void setNodes(Node right, Node down)
		{
			this.right = right;
			this.down = down;
			used = true;
		}

		@SideOnly(Side.CLIENT)
		public static class Slot
		{
			private Holder holder;
			private final int x;
			private final int y;
			private final int width;
			private final int height;
			private List<Slot> subSlots;

			public Slot(int x, int y, int width, int height)
			{
				this.x = x;
				this.y = y;
				this.width = width;
				this.height = height;
			}

			public Holder getStitchHolder()
			{
				return this.holder;
			}

			public int x()
			{
				return this.x;
			}

			public int y()
			{
				return this.y;
			}

			public boolean addSlot(Holder holderIn)
			{
				if (this.holder != null)
				{
					return false;
				}
				else
				{
					int width = holderIn.width();
					int height = holderIn.height();

					if (width == this.width && height == this.height)
					{
						this.holder = holderIn;
						return true;
					}
					if (width <= this.width && height <= this.height)
					{

						if (this.subSlots == null)
						{
							this.subSlots = Lists.newArrayListWithCapacity(1);
							this.subSlots.add(new Slot(this.x, this.y, width, height));
							int k = this.width - width;
							int l = this.height - height;

							if (l > 0 && k > 0)
							{
								int i1 = Math.max(this.height, k);
								int j1 = Math.max(this.width, l);

								if (i1 >= j1)
								{
									this.subSlots.add(new Slot(this.x, this.y + height, width, l));
									this.subSlots.add(new Slot(this.x + width, this.y, k, this.height));
								}
								else
								{
									this.subSlots.add(new Slot(this.x + width, this.y, k, height));
									this.subSlots.add(new Slot(this.x, this.y + height, this.width, l));
								}
							}
							else if (k == 0)
							{
								this.subSlots.add(new Slot(this.x, this.y + height, width, l));
							}
							else if (l == 0)
							{
								this.subSlots.add(new Slot(this.x + width, this.y, k, height));
							}
						}

						for (Slot slot : this.subSlots)
						{
							if (slot.addSlot(holderIn))
							{
								return true;
							}
						}

						return false;

					}
					else
					{
						return false;
					}
				}
			}

			/**
			 * Gets the slot and all its subslots
			 */
			public void getAllStitchSlots(List<Slot> slots)
			{
				if (this.holder != null)
				{
					slots.add(this);
				}
				else if (this.subSlots != null)
				{
					for (Slot slot : this.subSlots)
					{
						slot.getAllStitchSlots(slots);
					}
				}
			}

			public String toString()
			{
				return "Slot{x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture="
						+ this.holder + ", subSlots=" + this.subSlots + '}';
			}
		}
	}
}