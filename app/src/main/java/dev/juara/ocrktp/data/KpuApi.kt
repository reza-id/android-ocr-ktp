package dev.juara.ocrktp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface KpuApi {
    @GET("pemilih/dpt/1/hasil-cari/resultDps.json")
    suspend fun getDps(
        @Query("nik") nik: String,
        @Query("nama") nama: String = "",
        @Query("namaPropinsi") namaPropinsi: String = "",
        @Query("namaKabKota") namaKabKota: String = "",
        @Query("namaKecamatan") namaKecamatan: String = "",
        @Query("namaKelurahan") namaKelurahan: String = ""
    ) : KpuResponse
}