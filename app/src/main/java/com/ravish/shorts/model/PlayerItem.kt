package com.ravish.shorts.model

import com.google.android.exoplayer2.ExoPlayer

data class PlayerItem (
    var exoPlayer: ExoPlayer,
    var position: Int
    )