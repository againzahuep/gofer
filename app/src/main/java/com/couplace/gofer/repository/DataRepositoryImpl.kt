package com.couplace.gofer.repository

import android.annotation.SuppressLint
import android.content.Context
import com.couplace.gofer.R
import com.couplace.gofer.model.Product
import java.util.*

class DataRepositoryImpl : DataRepository {

    val data: MutableList<Product> = ArrayList()

    lateinit var context: Context

    init {
        this.data.add(Product("Astro BLACK Shoes",  "Shoes", context.resources.getDrawable(R.drawable.astro_black_side, null)))
        this.data.add(Product("ABRAHAM NAVY Shoes",  "Shoes", context.resources.getDrawable(R.drawable.abraham_navy_15024_side_20210708, null)))
        this.data.add(Product("AERO Shoes",  "Shoes", context.resources.getDrawable(R.drawable.aero_taupe_p45, null)))
        this.data.add(Product("AMELIA SKY Shoes",  "Shoes", context.resources.getDrawable(R.drawable.amelia_sky_blue_suede_p45, null)))
        this.data.add(Product("AMELIA WHITE",  "Shoes", context.resources.getDrawable(R.drawable.amelia_white_burgundy_p45, null)))
        this.data.add(Product("ARANGO MIDBROWN Shoes",  "Shoes", context.resources.getDrawable(R.drawable.aranga_midbrown_lace_ups_artesano_magnanni_p45, null)))
        this.data.add(Product("ARZUA COGNAC Shoes",  "Shoes", context.resources.getDrawable(R.drawable.arzua_cognac_side, null)))
        this.data.add(Product("BERNICE Shoes",  "Shoes", context.resources.getDrawable(R.drawable.bernice_taupe_p45, null)))
        this.data.add(Product("CARMEN Shoes",  "Shoes", context.resources.getDrawable(R.drawable.carmen_ii_black_p45, null)))
        this.data.add(Product("CHARLOTTE Shoes",  "Shoes", context.resources.getDrawable(R.drawable.charlotte_ii_green_suede_p45, null)))
        this.data.add(Product("ELENA Shoes",  "Shoes", context.resources.getDrawable(R.drawable.elena_taupe_p45, null)))
        this.data.add(Product("EVA BLACK Shoes",  "Shoes", context.resources.getDrawable(R.drawable.eva_black_p45, null)))
    }

    override fun add(value: Product) {
        this.data.add(value)
    }

    override fun addAll(value: List<Product>) {
        this.data.addAll(value)
    }

    override fun remove(index: Int) {
        if (index < 0 || index >= this.data.size) {
            throw IllegalAccessException("Index Is Invalid")
        }
        this.data.removeAt(index)
    }

    override fun get(): List<Product> {
        return this.data
    }

    override fun move(from: Int, to: Int) {
         this.data.removeAt(from).apply {
             data.add(to, this)
         }
    }

    override fun createProductsList(numProducts: Int, offset: Int): List<Product> {
        data.add(Product("EVA WHITE Shoes",  "Shoes", context.resources.getDrawable(R.drawable.eva_white_p45, null)))
        data.add(Product("GASOL TAUPE Shoes",  "Shoes", context.resources.getDrawable(R.drawable.gasol_taupe_suede_p45, null)))
        data.add(Product("HEIDI BLACK Shoes",  "Shoes", context.resources.getDrawable(R.drawable.heidi_black_suede_p45, null)))
        data.add(Product("ISADORA GRAY Shoes",  "Shoes", context.resources.getDrawable(R.drawable.isadora_grey_p45, null)))
        data.add(Product("KAEDE BLACK Shoes",  "Shoes", context.resources.getDrawable(R.drawable.kaede_black_p45, null)))
        data.add(Product("KENIA CREAM Shoes",  "Shoes", context.resources.getDrawable(R.drawable.kennia_cream_p45, null)))
        data.add(Product("KIARA BLACK Shoes",  "Shoes", context.resources.getDrawable(R.drawable.kiara_black_grey_p45, null)))
        data.add(Product("MARLI CUERO Shoes",  "Shoes", context.resources.getDrawable(R.drawable.marli_cuero_p45, null)))
        data.add(Product("NAVI SUEDE Shoes",  "Shoes", context.resources.getDrawable(R.drawable.stella_ii_navy_suede_p45, null)))
        data.add(Product("SAMOS COGNAC Shoes",  "Shoes", context.resources.getDrawable(R.drawable.samos_cognac_24140_side, null)))
        data.add(Product("STELLA TORBA Shoes",  "Shoes", context.resources.getDrawable(R.drawable.stella_iv_torba_p45, null)))
        data.add(Product("STELLA BLACK Shoes",  "Shoes", context.resources.getDrawable(R.drawable.stella_v_black_p45, null)))
        data.add(Product("THERESA WHITE Shoes",  "Shoes", context.resources.getDrawable(R.drawable.theresa_white_red_p45, null)))

        return this.data
    }
}