package com.couplace.gofer.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.couplace.gofer.R
import com.couplace.gofer.model.Product
import java.util.*
import javax.inject.Inject

class DataRepositoryImpl : DataRepository {

    var data = MutableLiveData<List<Product>>()

    @Inject
    lateinit var context: Context

    init {
        this.data.apply {
            listOf(
                Product(
                    "Astro BLACK Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.astro_black_side, null)!!
                ),
                Product(
                    "ABRAHAM NAVY Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.abraham_navy_15024_side_20210708, null)!!
                ),
                Product(
                    "AERO Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.aero_taupe_p45, null)!!
                ),
                Product(
                    "AMELIA SKY Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.amelia_sky_blue_suede_p45, null)!!
                ),
                Product(
                    "AMELIA WHITE",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.amelia_white_burgundy_p45, null)!!
                ),
                Product(
                    "ARANGO MIDBROWN Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.aranga_midbrown_lace_ups_artesano_magnanni_p45,null)!!
                ),
                Product(
                    "ARZUA COGNAC Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.arzua_cognac_side, null)!!
                ),
                Product(
                    "BERNICE Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.bernice_taupe_p45, null)!!
                ),
                Product(
                    "CARMEN Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.carmen_ii_black_p45, null)!!
                ),
                Product(
                    "CHARLOTTE Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.charlotte_ii_green_suede_p45, null)!!
                ),
                Product(
                    "ELENA Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.elena_taupe_p45, null)!!
                ),
                Product(
                    "EVA BLACK Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.eva_black_p45, null)!!
                )
            )

        }
    }

    override fun add(value: Product) {
        this.data.postValue(listOf(value))
    }

    override fun addAll(value: List<Product>) {
        this.data.postValue(value)
}
    override fun get(): List<Product> {
        return this.data.value!!
    }



    override fun createProductsList(numProducts: Int, offset: Int): List<Product> {
        this.data.apply {
            listOf(
                Product(
                    "EVA WHITE Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(context.resources, R.drawable.eva_white_p45, null)!!
                ),
                Product(
                    "GASOL TAUPE Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.gasol_taupe_suede_p45,
                        null
                    )!!
                ),
                Product(
                    "HEIDI BLACK Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.heidi_black_suede_p45,
                        null
                    )!!
                ),
                Product(
                    "ISADORA GRAY Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.isadora_grey_p45,
                        null
                    )!!
                ),
                Product(
                    "KAEDE BLACK Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.kaede_black_p45,
                        null
                    )!!
                ),
                Product(
                    "KENIA CREAM Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.kennia_cream_p45,
                        null
                    )!!
                ),
                Product(
                    "KIARA BLACK Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.kiara_black_grey_p45,
                        null
                    )!!
                ),
                Product(
                    "MARLI CUERO Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.marli_cuero_p45,
                        null
                    )!!
                ),
                Product(
                    "NAVI SUEDE Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.stella_ii_navy_suede_p45,
                        null
                    )!!
                ),
                Product(
                    "SAMOS COGNAC Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.samos_cognac_24140_side,
                        null
                    )!!
                ),
                Product(
                    "STELLA TORBA Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.stella_iv_torba_p45,
                        null
                    )!!
                ),
                Product(
                    "STELLA BLACK Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.stella_v_black_p45,
                        null
                    )!!
                ),
                Product(
                    "THERESA WHITE Shoes",
                    "Shoes",
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.theresa_white_red_p45,
                        null
                    )!!
                )
            )
        }

        return this.data.value!!
    }
}