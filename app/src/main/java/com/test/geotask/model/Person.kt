package com.test.geotask.model

import com.test.geotask.R

data class Person(
    val id: Int,
    var avatar: Int,
    var name: String,
    var longitude : Float,
    var latitude: Float,
    var distance: String
)

var personList = listOf(
    Person(
        0,
        R.drawable.me_avatar,
        "Me",
        0.000000f,
        0.000000f,
        "0"
    ),
    Person(
        1,
        R.drawable.female1,
        "Kate Lebovski",
        23.542313f,
        12.233264f,
        "0"
    ),
    Person(
        2,
        R.drawable.female2,
        "Rosa Alisha",
        22.378553f,
        43.706665f,
        "0"
    ),
    Person(
        3,
        R.drawable.female3,
        "Polly Bethan",
        39.096863f,
        -38.561195f,
        "0"
    ),
    Person(4,
        R.drawable.female4,
        "Ella Ellie",
        25.537092f,
        24.760067f,
        "0"
    ),
    Person(
        5,
        R.drawable.female5,
        "Caitlin Abby",
        -25.867289f,
        -19.159951f,
        "0"
    ),
    Person(
        6,
        R.drawable.female6,
        "Caitlin Abby",
        -25.867289f,
        -19.159951f,
        "0",
    ),
    Person(
        7,
        R.drawable.female7,
        "Polly Bethan",
        39.096863f,
        -38.561195f,
        "0",
    ),
    Person(
        8,
        R.drawable.male1,
        "Herbert Jonah",
        -49.331025f,
        83.796992f,
        "0",
    ),
    Person(
        9,
        R.drawable.male2,
        "Shawn Gerald",
        3.798198f,
        -26.808636f,
        "0",
    ),
    Person(
        10,
        R.drawable.male3,
        "Abraham Kyran",
        84.025759f,
        -51.326998f,
        "0",
    ),
    Person(
        11,
        R.drawable.male4,
        "Todd Zac",
        -21.341833f,
        -7.461737f,
        "0",
    ),
    Person(
        12,
        R.drawable.male5,
        "Edwin Eden",
        88.493751f,
        46.448197f,
        "0",
    ),
    Person(
        13,
        R.drawable.male6,
        "Taylor Steven",
        88.493751f,
        46.448197f,
        "0",
    ),
    Person(
        14,
        R.drawable.male7,
        "Cleo Elmer",
        28.44501f,
        52.182461f,
        "0",
    ),
)
