package com.example.gabsstudentstay.data.seed

import com.example.gabsstudentstay.data.local.entity.ListingEntity
import com.example.gabsstudentstay.data.local.entity.StudentEntity

object SeedData {

    fun students(): List<StudentEntity> {
        val createdAt = System.currentTimeMillis()
        val names = listOf(
            "Tebogo Mothibi", "Kagiso Moagi", "Boitumelo Sechele", "Onalenna Dube",
            "Thato Moremi", "Neo Ramotswa", "Mpho Seleke", "Lorato Kebakile",
            "Katlego Mooketsi", "Keneilwe Molefi", "Bakang Seretse", "Goitsemodimo Ramatlhodi",
            "Keabetswe Tshukudu", "Oarabile Thipe", "Bontle Kgosidialwa", "Tshepo Ndlovu",
            "Naledi Mogapi", "Lesego Matlapeng", "Kabelo Mmereki", "Tumisang Masire",
            "Refilwe Motlhatlhedi", "Tiro Bantsi", "Karabo Modise", "Thatayaone Pheto",
            "Amogelang Pule", "Masego Sebego", "Olebogeng Moffat", "Mmapula Kedirile",
            "Dineo Rantao", "Kefilwe Letsholo", "Lebogang Tshekiso", "Kutlwano Mathiba",
            "Phenyo Tiro", "Bokamoso Kabi", "Goitsemang Kelebetseng", "Pako Motswagole",
            "Seneo Mooko", "Oneile Molale", "Bakang Tlhopho", "Molemo Kgamanyane",
            "Keorapetse Makgosa", "Rorisang Kgabo", "Tshegofatso Botho", "Boipelo Tsabeng",
            "Keitumetse Mophuting", "Onkabetse Gaborone", "Aobakwe Seema", "Phetogo Kwele",
            "Segametsi Mmolawa", "Chuma Mokobi"
        )

        return names.mapIndexed { index, fullName ->
            StudentEntity(
                fullName = fullName,
                email = "student${index + 1}@gabsstudentstay.com",
                phoneNumber = "71 ${String.format("%03d %03d", index + 1, index + 51)}",
                password = "password123",
                role = if (index in listOf(9, 19, 29, 39, 49)) "Provider" else "Student",
                createdAt = createdAt
            )
        }
    }

    fun listings(): List<ListingEntity> {
        val areas = listOf(
            "Block 6", "Village", "Tlokweng", "Gaborone West", "Broadhurst",
            "Phakalane", "Extension 2", "Mogoditshane", "Block 8", "Main Mall",
            "Partial", "Maruapula", "Kgale View", "Extension 10", "Bontleng"
        )
        val roomTypes = listOf("Single Room", "Bedsitter", "Studio", "Shared Apartment", "Cottage Room")
        val campuses = listOf(
            "Botswana Accountancy College",
            "University of Botswana",
            "Botho University",
            "Limkokwing University",
            "Gaborone Technical College"
        )
        val amenitiesOptions = listOf(
            "WiFi, Water Included, Security, Furnished",
            "WiFi, Parking, Wardrobe, Electricity Included",
            "Water Included, Security, Study Desk, Furnished",
            "WiFi, Laundry Area, Parking, Security",
            "Furnished, Water Included, Ceiling Fan, WiFi"
        )
        val providerNames = listOf(
            "Mma Ditiro", "Kabelo Property Services", "Tshiamo Rentals", "Pula Rooms",
            "Masa Student Housing", "Lorato Lettings", "Tebelo Homes", "Boago Spaces"
        )
        val providerPhones = listOf(
            "72 410 101", "73 520 202", "74 630 303", "75 740 404",
            "76 850 505", "77 960 606", "71 170 707", "72 280 808"
        )
        val baseLatitudes = mapOf(
            "Block 6" to -24.6460,
            "Village" to -24.6545,
            "Tlokweng" to -24.6715,
            "Gaborone West" to -24.6665,
            "Broadhurst" to -24.6350,
            "Phakalane" to -24.5950,
            "Extension 2" to -24.6580,
            "Mogoditshane" to -24.6260,
            "Block 8" to -24.6505,
            "Main Mall" to -24.6552,
            "Partial" to -24.6420,
            "Maruapula" to -24.6340,
            "Kgale View" to -24.6880,
            "Extension 10" to -24.6468,
            "Bontleng" to -24.6628
        )
        val baseLongitudes = mapOf(
            "Block 6" to 25.9070,
            "Village" to 25.9165,
            "Tlokweng" to 25.9680,
            "Gaborone West" to 25.8890,
            "Broadhurst" to 25.9300,
            "Phakalane" to 25.9890,
            "Extension 2" to 25.9130,
            "Mogoditshane" to 25.8650,
            "Block 8" to 25.9250,
            "Main Mall" to 25.9092,
            "Partial" to 25.9010,
            "Maruapula" to 25.9440,
            "Kgale View" to 25.8780,
            "Extension 10" to 25.9380,
            "Bontleng" to 25.9205
        )
        val distances = listOf(0.8, 1.2, 1.5, 1.9, 2.3, 2.8, 3.1, 3.6, 4.0, 4.7)
        val prices = listOf(
            1200.0, 1300.0, 1450.0, 1500.0, 1600.0, 1750.0, 1800.0, 1900.0, 2000.0, 2100.0,
            2200.0, 2300.0, 2400.0, 2500.0, 2600.0, 2700.0, 2800.0, 2900.0, 3000.0, 3100.0,
            3200.0, 3300.0, 3400.0, 3500.0, 3600.0, 3700.0, 3800.0, 3900.0, 4000.0, 4200.0,
            4500.0
        )
        val availableDates = listOf(
            "2026-06-01", "2026-06-05", "2026-06-10", "2026-06-15", "2026-06-20",
            "2026-07-01", "2026-07-05", "2026-07-10", "2026-07-15", "2026-08-01"
        )

        return List(50) { index ->
            val area = areas[index % areas.size]
            val roomType = roomTypes[index % roomTypes.size]
            val campus = campuses[index % campuses.size]
            val amenities = amenitiesOptions[index % amenitiesOptions.size]
            val price = prices[index % prices.size]
            val lat = baseLatitudes.getValue(area) + ((index % 4) * 0.0012)
            val lng = baseLongitudes.getValue(area) + ((index % 3) * 0.0015)

            ListingEntity(
                title = "${roomType} in $area",
                price = price,
                location = area,
                roomType = roomType,
                amenities = amenities,
                availabilityDate = availableDates[index % availableDates.size],
                depositAmount = if (index % 3 == 0) price / 2 else price,
                imageName = "room_${(index % 10) + 1}",
                status = if (index in listOf(6, 13, 21, 34, 47)) "Reserved" else "Available",
                providerName = providerNames[index % providerNames.size],
                providerPhone = providerPhones[index % providerPhones.size],
                campusName = campus,
                distanceFromCampusKm = distances[index % distances.size],
                latitude = lat,
                longitude = lng,
                description = "Comfortable $roomType located in $area, suitable for students studying near $campus. Includes $amenities and convenient access to shops and transport."
            )
        }
    }
}
