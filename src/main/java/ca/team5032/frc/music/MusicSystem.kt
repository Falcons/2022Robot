package ca.team5032.frc.music

import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.music.Orchestra
import java.util.*

/**
 * Plays music with the Falcon 500s.
 *
 * @param falcons List of falcons to use as instruments.
 */
class MusicSystem(falcons: Collection<TalonFX>) {

    // The queue of songs to play.
    private val queue: LinkedList<Song> = LinkedList()

    // The Orchestra from CTRE, used for playing .chrp files on the Falcon500s.
    private val orchestra: Orchestra = Orchestra(falcons)

    fun loop() {
        // If there's no song playing and the queue isn't empty, cycle to the next song.
        if (!this.orchestra.isPlaying && !this.queue.isEmpty()) {
            this.cycle()
        }
    }

    /**
     * Loads a song to be played.
     *
     * @param song The song to be loaded.
     */
    fun load(song: Song) {
        orchestra.loadMusic(song.file)
    }

    /**
     * Plays the currently loaded song.
     */
    fun play() {
        orchestra.play()
    }

    /**
     * Cycles to the next song.
     */
    fun cycle() {
        val song = queue.poll() ?: return

        load(song)
    }

    /**
     * Forces a song to the front of the queue.
     *
     * @param song The song to force-queue.
     */
    fun forceQueue(song: Song) {
        queue.addFirst(song)
    }

    /**
     * Queues a song to be played.
     *
     * @param song The song to queue.
     */
    fun queue(song: Song) {
        queue.add(song)
    }

}