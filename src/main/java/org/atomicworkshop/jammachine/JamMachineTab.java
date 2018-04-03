package org.atomicworkshop.jammachine;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.atomicworkshop.jammachine.libraries.BlockLibrary;

public class JamMachineTab extends CreativeTabs
{
	private ItemStack tabIcon = null;

	JamMachineTab() {
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
