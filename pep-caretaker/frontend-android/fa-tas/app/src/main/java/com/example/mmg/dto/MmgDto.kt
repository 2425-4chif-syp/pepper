package com.example.mmg.dto

import com.google.gson.annotations.SerializedName

data class MmgDto(
    @SerializedName("gameType")val gameType: GameType,
    @SerializedName("id")val id: Int,
    @SerializedName("name")val name: String,
    @SerializedName("storyIcon")val storyIcon: StoryIcon
)

data class GameType(
    @SerializedName("id")val id: String,
    @SerializedName("name")val name: String
)

data class StoryIcon(
    @SerializedName("description")val description: String,
    @SerializedName("id")val id: Int,
)