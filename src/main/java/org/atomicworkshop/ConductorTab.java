package org.atomicworkshop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.atomicworkshop.libraries.BlockLibrary;

public class ConductorTab extends CreativeTabs
{
	private ItemStack tabIcon = null;

	ConductorTab() {
		super(Reference.TabLabel);
	}

	@Override
	public ItemStack getTabIconItem()
	{
		if (tabIcon == null) {
			tabIcon = new ItemStack(BlockLibrary.sequencer);
		}
		return tabIcon;
	}
}
