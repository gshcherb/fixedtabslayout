package com.gshcherb.fixedtabs

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

private const val DEFAULT_TABS_COUNT = 3

class FixedTabsLayout : TabLayout {
    private var attrTabsCount: Int = DEFAULT_TABS_COUNT

    private var adapterObserver: FixedTabsDataSetObserver? = null

    constructor(ctx: Context) : super(ctx) {
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        init(attrs)
    }

    constructor(ctx: Context, attrs: AttributeSet, attrsDef: Int) : super(ctx, attrs, attrsDef) {
        init(attrs, attrsDef)
    }


    private fun init(attrs: AttributeSet? = null, attrsDef: Int = 0) {
        attrs.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.FixedTabsLayout, attrsDef, 0)
            attrTabsCount = Math.max(DEFAULT_TABS_COUNT,
                    a.getInt(R.styleable.FixedTabsLayout_ftl_tabsCount, DEFAULT_TABS_COUNT))
            a.recycle()
        }
    }

    fun setupTabs(viewPager: ViewPager) {
        setupDataSetObserver(viewPager)
        setupOnPageChangeListener(viewPager)
        setupOnTabSelectedListener(viewPager)
    }

    private fun setupDataSetObserver(viewPager: ViewPager) {
        viewPager.addOnAdapterChangeListener { _: ViewPager, oldAdapter: PagerAdapter?, newAdapter: PagerAdapter? ->
            oldAdapter?.let { adapter ->
                adapterObserver?.let { adapter.unregisterDataSetObserver(it) }
            }

            newAdapter?.run {
                adapterObserver = FixedTabsDataSetObserver(this@FixedTabsLayout, viewPager)
                registerDataSetObserver(adapterObserver!!)
                fillTabs(viewPager, count)
            }
        }
        viewPager.adapter?.run { fillTabs(viewPager, count) }
    }

    private fun setupOnPageChangeListener(viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                initTitles(viewPager)
            }
        })
    }

    private fun setupOnTabSelectedListener(viewPager: ViewPager) {
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabReselected(tab: Tab) {
            }

            override fun onTabUnselected(tab: Tab) {
            }

            override fun onTabSelected(tab: Tab) {
                tab.tag?.let {
                    viewPager.currentItem = it.toString().toInt()
                }
            }
        })
    }

    internal fun fillTabs(viewPager: ViewPager, count: Int) {
        removeAllTabs()
        for (i in 1..Math.min(attrTabsCount, count)) {
            addTab(
                    newTab().setText(""), false
            )
        }
        initTitles(viewPager)
    }

    internal fun initTitles(viewPager: ViewPager) {
        val currentAdapterItem = viewPager.currentItem
        viewPager.adapter?.run {
            when (currentAdapterItem) {
                0 -> movingToFirstAdapterItem(viewPager.adapter!!)
                count - 1 -> movingToLastAdapterItem(viewPager.adapter!!)
                else -> movingToMidAdapterItem(viewPager.adapter!!, currentAdapterItem)
            }
        }
    }

    private fun movingToFirstAdapterItem(adapter: PagerAdapter) {
        getTabAt(0)!!.select()
        for (index in 0 until tabCount) {
            getTabAt(index)?.let { setTabInfo(it, adapter, index) }
        }
    }

    private fun movingToLastAdapterItem(adapter: PagerAdapter) {
        getTabAt(tabCount - 1)!!.select()
        for (index in 0 until tabCount) {
            getTabAt(index)?.let { setTabInfo(it, adapter, adapter.count - tabCount + index) }
        }
    }

    private fun movingToMidAdapterItem(adapter: PagerAdapter, currentAdapterItem: Int) {
        val prevAdapterItem = getTabAt(selectedTabPosition)?.tag?.toString()?.toInt() ?: 0

        if (Math.abs(prevAdapterItem - currentAdapterItem) in 0..1) {
            handleSwipe(adapter, prevAdapterItem, currentAdapterItem)
        } else {
            handleJump(adapter, currentAdapterItem)
        }
    }

    private fun handleSwipe(adapter: PagerAdapter, prevAdapterItem: Int, currentAdapterItem: Int) {
        when (selectedTabPosition) {
            0 ->            /**/ handleCurrentFirstSelected(adapter, prevAdapterItem, currentAdapterItem)
            tabCount - 1 -> /**/ handleCurrentLastSelected(adapter, prevAdapterItem, currentAdapterItem)
            else ->         /**/ handleCurrentMidSelected(adapter, prevAdapterItem, currentAdapterItem)
        }
    }

    private fun handleJump(adapter: PagerAdapter, currentAdapterItem: Int) {
        // we might need to do smart positioning in the middle here
        if (currentAdapterItem == 0) {
            reinitializeTitles(adapter, 0)
            getTabAt(0)!!.select()
        } else {
            reinitializeTitles(adapter, currentAdapterItem - 1)
            getTabAt(1)!!.select()
        }
    }

    private fun handleCurrentFirstSelected(adapter: PagerAdapter, old: Int, new: Int) {
        if (new > old) {
            reinitializeTitles(adapter, old)
            getTabAt(1)!!.select()
        } else {
            reinitializeTitles(adapter, new)
        }
    }

    private fun handleCurrentMidSelected(adapter: PagerAdapter, old: Int, new: Int) {
        reinitializeTitles(adapter, old - selectedTabPosition)
        if (new > old) {
            getTabAt(selectedTabPosition + 1)!!.select()
        } else if (new < old) {
            getTabAt(selectedTabPosition - 1)!!.select()
        }
    }

    private fun handleCurrentLastSelected(adapter: PagerAdapter, old: Int, new: Int) {
        if (new < old) {
            reinitializeTitles(adapter, old - tabCount + 1)
            getTabAt(tabCount - 1 - 1)!!.select()
        } else {
            reinitializeTitles(adapter, new - tabCount + 1)
        }
    }

    private fun reinitializeTitles(adapter: PagerAdapter, first: Int) {
        // have to re-initialize titles every swipe, because tabs could be invalidated
        for (index in 0 until tabCount) {
            getTabAt(index)?.let { setTabInfo(it, adapter, first + index) }
        }
    }

    private fun setTabInfo(tab: TabLayout.Tab, adapter: PagerAdapter, index: Int) {
        tab.text = adapter.getPageTitle(index)
        tab.tag = index
    }
}