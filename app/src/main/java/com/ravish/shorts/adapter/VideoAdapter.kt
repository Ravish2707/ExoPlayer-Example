package com.ravish.shorts.adapter

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.ravish.shorts.R
import com.ravish.shorts.databinding.LayoutSingleVideoBinding
import com.ravish.shorts.model.PlayerItem
import com.ravish.shorts.model.Video

class VideoAdapter(
    private val context: Context,
    private val videos: List<Video>,
    private val videoPrepareListener: OnVideoPrepareListener
    ): RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = LayoutSingleVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val binding = holder.mBinding
        val video = videos[position]

        binding.tvVideoTitle.text = video.title
        binding.playerView.useController = false
        val player = ExoPlayer.Builder(context).build()
        player.addListener(object: Player.Listener{
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.d("VideoAdapter", error.message.toString())
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (playbackState == Player.STATE_BUFFERING) {
                    binding.pbPlayer.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    binding.pbPlayer.visibility = View.GONE
                }
            }
        })

        binding.playerView.player = player
        playVideo(video.videoUrl, player)
        videoPrepareListener.onVideoPrepared(PlayerItem(player, position))

        binding.clMainPlayer.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                player.playWhenReady = false
                binding.btnPlayPause.visibility = View.VISIBLE
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                player.playWhenReady = true
                player.play()
                binding.btnPlayPause.visibility = View.VISIBLE
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    binding.btnPlayPause.visibility = View.GONE
                }, 2000)
            }
        }
    }

    inner class VideoViewHolder(val mBinding: LayoutSingleVideoBinding) : RecyclerView.ViewHolder(mBinding.root)

    private fun playVideo(videoUrl: String, player: ExoPlayer) {
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.playWhenReady = true
        player.play()
    }

    interface OnVideoPrepareListener {
        fun onVideoPrepared(playerItem: PlayerItem)
    }
}