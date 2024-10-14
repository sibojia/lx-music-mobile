import { NativeModules } from 'react-native'

const { BluetoothLyricModule } = NativeModules

/**
 * play lyric
 * @param {Number} time play time
 * @returns {Promise} Promise
 */
export const play = async(time: number): Promise<void> => {
  // if (!isShowLyric) return Promise.resolve()
  return BluetoothLyricModule.play(time)
}

/**
 * pause lyric
 */
export const pause = async(): Promise<void> => {
  // if (!isShowLyric) return Promise.resolve()
  return BluetoothLyricModule.pause()
}

/**
 * set lyric
 * @param lyric lyric str
 * @param title song title
 * @param singer song artist
 * @param album song album
 */
export const setLyric = async(lyric: string, title: string, singer: string, album: string): Promise<void> => {
  // if (!isShowLyric) return Promise.resolve()
  return BluetoothLyricModule.setLyric(lyric, title || '', singer || '', album || '')
}

export const setPlaybackRate = async(rate: number): Promise<void> => {
  // if (!isShowLyric) return Promise.resolve()
  return BluetoothLyricModule.setPlaybackRate(rate)
}

export const toggleSendBluetoothLyric = async(enable: boolean): Promise<void> => {
  return BluetoothLyricModule.toggleSendBluetoothLyric(enable)
}

