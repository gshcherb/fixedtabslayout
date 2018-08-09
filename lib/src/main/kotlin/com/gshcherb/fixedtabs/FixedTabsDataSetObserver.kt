package com.gshcherb.fixedtabs

import android.database.DataSetObserver
import androidx.viewpager.widget.ViewPager

class FixedTabsDataSetObserver(
        private val tabsLayout: FixedTabsLayout,
        private val viewPager: ViewPager
) : DataSetObserver() {
    override fun onChanged() {
        super.onChanged()
        viewPager.adapter?.run { tabsLayout.fillTabs(viewPager, count) }
    }

    override fun onInvalidated() {
        super.onInvalidated()
        viewPager.adapter?.run { tabsLayout.fillTabs(viewPager, count) }
    }
}
