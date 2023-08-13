package com.test.geotask

data class PeopleItem(
    val id: Int,
    val avatar: Int,
    val name: String,
    var longitude : Float,
    var latitude: Float,
    var distance: String,
    var isPersonChosen: Boolean = false,
)

var personList = listOf(
    PeopleItem(
        0,
        R.drawable.me_avatar,
        "Me",
        0.000000f,
        0.000000f,
        "0",
        false,
    ),
    PeopleItem(
        1,
        R.drawable.female1,
        "Kate Lebovski",
        23.542313f,
        12.233264f,
        "0",
        false
    ),
    PeopleItem(
        2,
        R.drawable.female2,
        "Rosa Alisha",
        22.378553f,
        43.706665f,
        "0",
        false
    ),
    PeopleItem(
        3,
        R.drawable.female3,
        "Polly Bethan",
        39.096863f,
        -38.561195f,
        "0",
        false
    ),
    PeopleItem(4,
        R.drawable.female4,
        "Ella Ellie",
        25.537092f,
        24.760067f,
        "0",
        false
    ),
    PeopleItem(
        5,
        R.drawable.female5,
        "Caitlin Abby",
        -25.867289f,
        -19.159951f,
        "0",
        false
    ),
    PeopleItem(
        6,
        R.drawable.female6,
        "Caitlin Abby",
        -25.867289f,
        -19.159951f,
        "0",
        false
    ),
    PeopleItem(
        7,
        R.drawable.female7,
        "Polly Bethan",
        39.096863f,
        -38.561195f,
        "0",
        false
    ),
    PeopleItem(
        8,
        R.drawable.male1,
        "Herbert Jonah",
        -49.331025f,
        83.796992f,
        "0",
        false
    ),
    PeopleItem(
        9,
        R.drawable.male2,
        "Shawn Gerald",
        3.798198f,
        -26.808636f,
        "0",
        false
    ),
    PeopleItem(
        10,
        R.drawable.male3,
        "Abraham Kyran",
        84.025759f,
        -51.326998f,
        "0",
        false
    ),
    PeopleItem(
        11,
        R.drawable.male4,
        "Todd Zac",
        -21.341833f,
        -7.461737f,
        "0",
        false
    ),
    PeopleItem(
        12,
        R.drawable.male5,
        "Edwin Eden",
        88.493751f,
        46.448197f,
        "0",
        false
    ),
    PeopleItem(
        13,
        R.drawable.male6,
        "Taylor Steven",
        88.493751f,
        46.448197f,
        "0",
        false
    ),
    PeopleItem(
        14,
        R.drawable.male7,
        "Cleo Elmer",
        28.44501f,
        52.182461f,
        "0",
        false
    ),
)
