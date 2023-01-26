package com.pcs.apptoko

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pcs.apptoko.LoginActivity.Companion.sessionManager
import com.pcs.apptoko.adapter.ProdukAdapter
import com.pcs.apptoko.api.BaseRetrofit
import com.pcs.apptoko.response.produk.Produk
import com.pcs.apptoko.response.produk.ProdukResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdukFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endPoint }
    private val rvAdapter = ProdukAdapter()
    private val listProduk = mutableListOf<Produk>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_produk, container, false)

        val rv = view.findViewById(R.id.rv_produk) as RecyclerView
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = rvAdapter

        val spinner = view.findViewById<Spinner>(R.id.urutan)
        val sorts = resources.getStringArray(R.array.urutan)
        val adapter = ArrayAdapter(requireContext(), R.layout.urutan, sorts)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> rvAdapter.urutHarga(false)
                    1 -> rvAdapter.urutHarga(true)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        getProduk(view)

        val btnTambah = view.findViewById<Button>(R.id.btnTambah)
        btnTambah.setOnClickListener {
            Toast.makeText(activity?.applicationContext, "Click", Toast.LENGTH_LONG).show()

            val bundle = Bundle()
            bundle.putString("status", "tambah")

            findNavController().navigate(R.id.produkFormFragment, bundle)
        }


        return view
    }

    fun getProduk(view: View) {
        val token = sessionManager.getString("TOKEN")

        api.getProduk(token.toString()).enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(
                call: Call<ProdukResponse>,
                response: Response<ProdukResponse>,
            ) {
                Log.d("ProdukData", response.body().toString())

                response.body()?.data?.produk?.let {
                    val txtTotalProduk = view.findViewById(R.id.txtTotalProduk) as TextView
                    txtTotalProduk.text = it.size.toString() + " Item"

                    listProduk.clear()
                    listProduk.addAll(it)
                    rvAdapter.setProduk(it)
                }
            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Log.e("ProdukError", t.toString())
            }

        })
    }

}