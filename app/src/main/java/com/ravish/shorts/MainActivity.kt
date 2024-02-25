package com.ravish.shorts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.ravish.shorts.adapter.VideoAdapter
import com.ravish.shorts.databinding.ActivityMainBinding
import com.ravish.shorts.model.PlayerItem
import com.ravish.shorts.model.Video

class MainActivity : AppCompatActivity(), VideoAdapter.OnVideoPrepareListener {

    private lateinit var mBinding: ActivityMainBinding

    private val videos = listOf(
        Video("Video 1", "Description 1", "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4"),
        Video("Video 2", "Description 2", "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4"),
        Video("Video 3", "Description 2", "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4"),
        Video("Video 4", "Description 2", "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4"),
        Video("Video 5", "Description 2", "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4"),
        Video("Video 6", "Description 2", "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4"),
    )

    private var playerItems = arrayListOf<PlayerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val videoAdapter = VideoAdapter(applicationContext, videos, this)
        mBinding.videoViewPager.adapter = videoAdapter

        mBinding.videoViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val previousIndex = playerItems.indexOfFirst { it.exoPlayer.isPlaying }
                if (previousIndex != -1) {
                    val player = playerItems[previousIndex].exoPlayer
                    player.pause()
                    player.playWhenReady = false
                }
                val newIndex = playerItems.indexOfFirst { it.position == position }
                if (newIndex != -1) {
                    val player = playerItems[newIndex].exoPlayer
                    player.playWhenReady = true
                    player.play()
                }
            }
        })

    }

    override fun onVideoPrepared(playerItem: PlayerItem) {
        playerItems.add(playerItem)
    }

    override fun onPause() {
        super.onPause()
        val index = playerItems.indexOfFirst { it.position == mBinding.videoViewPager.currentItem }
        if (index != -1) {
            val player = playerItems[index].exoPlayer
            player.pause()
            player.playWhenReady = false
        }
    }

    override fun onResume() {
        super.onResume()
        val index = playerItems.indexOfFirst { it.position == mBinding.videoViewPager.currentItem }
        if (index != -1) {
            val player = playerItems[index].exoPlayer
            player.playWhenReady = true
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(playerItems.isNotEmpty()) {
            for (item in playerItems) {
                val player = item.exoPlayer
                player.stop()
                player.clearMediaItems()
            }
        }
    }
}