import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

object IpUtil {

    fun addBinaryNumber(first: String, second: String): String {
        val result = StringBuilder("")
        var sum = 0
        var i: Int = first.length - 1
        var j: Int = second.length - 1
        while (i >= 0 || j >= 0 || sum == 1) {
            sum += if (i >= 0) first.toCharArray()[i] - '0' else 0
            sum += if (j >= 0) second.toCharArray()[j] - '0' else 0
            result.append((sum % 2 + '0'.code).toChar())
            sum /= 2
            i--
            j--
        }
        return result.reverse().toString()
    }

    fun toBinary(strIpAddress: String): String {
        val binary = StringBuilder()
        val octants = strIpAddress.split(".")
        for (octant in octants) {
            var bin = octant.toInt().toString(2)
            if (bin.length < 8) {
                val leadingZeroCount = 8 - bin.length
                val octantBin = StringBuilder()
                for (i in 1..leadingZeroCount) {
                    octantBin.append('0')
                }
                octantBin.append(bin)
                bin = octantBin.toString()
            }
            binary.append(bin)
        }
        return binary.toString()
    }

    fun fromBinary(binary: String): String {
        val masks = mutableListOf<String>()
        for (i in 1..4) {
            val endIndex = i * 8
            val startIndex = endIndex - 8
            val octantMask = binary.substring(startIndex, endIndex).toInt(2)
            masks.add(octantMask.toString())
        }
        return masks.joinToString(".")
    }

    fun closestHost(count: Int): Int {
        val bits = getHostBitSize(count)
        return getHostCount(bits)
    }

    fun getHostBitSize(hosts: Int): Int {
        return ceil(ln(hosts.toDouble()) / ln(2f)).toInt()
    }

    fun getHostCount(bit: Int): Int {
        return 2f.pow(bit).toInt()
    }

}