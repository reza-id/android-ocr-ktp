package dev.juara.ocrktp.data
import com.google.gson.annotations.SerializedName


data class KpuResponse(
    @SerializedName("aaData")
    val aaData: List<AaData>,
    @SerializedName("iTotalRecords")
    val iTotalRecords: Int
)

data class AaData(
    @SerializedName("jenisKelamin")
    val jenisKelamin: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("namaKabKota")
    val namaKabKota: String,
    @SerializedName("namaKecamatan")
    val namaKecamatan: String,
    @SerializedName("namaKelurahan")
    val namaKelurahan: String,
    @SerializedName("namaPropinsi")
    val namaPropinsi: String,
    @SerializedName("nik")
    val nik: String,
    @SerializedName("tps")
    val tps: Int
)