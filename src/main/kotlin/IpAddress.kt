data class IpAddress(
    val address: String,
    val netBit: Int,
    val hostBit: Int
) {
    fun availableHosts(): Int {
        if (isNotValid()) throw Exception("IP Address is not valid")
        return IpUtil.getHostCount(hostBit)
    }

    fun assignableHosts(): Int {
        if (isNotValid()) throw Exception("IP Address is not valid")
        return IpUtil.getHostCount(hostBit) - 2
    }

    fun getFirstAddress(): String {
        if (isNotValid()) throw Exception("IP Address is not valid")
        val addressOctant = address.split(".").map { it.toInt() }
        val subnetMaskOctant = geSubnetMask().split(".").map { it.toInt() }
        val octant = mutableListOf<String>()
        for (i in 0..3) {
            octant.add((addressOctant[i] and subnetMaskOctant[i]).toString())
        }
        return octant.joinToString(".")
    }

    fun getLastAddress(): String {
        if (isNotValid()) throw Exception("IP Address is not valid")
        val addressOctant = address.split(".").map { it.toInt() }
        val wildCardMaskOctant = geWildCartMask().split(".").map { it.toInt() }
        val octant = mutableListOf<String>()
        for (i in 0..3) {
            octant.add((addressOctant[i] or wildCardMaskOctant[i]).toString())
        }
        return octant.joinToString(".")
    }

    fun geSubnetMask(): String {
        if (isNotValid()) throw Exception("IP Address is not valid")
        var bits = ""
        for (i in 1..netBit) {
            bits += "1"
        }
        for (i in 1..hostBit) {
            bits += "0"
        }
        return IpUtil.fromBinary(bits)
    }

    private fun geWildCartMask(): String {
        if (isNotValid()) throw Exception("IP Address is not valid")
        var bits = ""
        for (i in 1..netBit) {
            bits += "0"
        }
        for (i in 1..hostBit) {
            bits += "1"
        }
        return IpUtil.fromBinary(bits)
    }

    fun isDivisible(hosts: Int): Boolean {
        val needHostBit = IpUtil.getHostBitSize(hosts)
        return needHostBit < hostBit
    }

    fun divideSubAddresses(hosts: Int): List<IpAddress> {
        val hostBit = IpUtil.getHostBitSize(hosts)
        val subnetBit = 32 - (netBit + hostBit)
        val subnetCount = IpUtil.getHostCount(subnetBit)
        val binary = IpUtil.toBinary(address)
        val subnets = mutableListOf<IpAddress>()
        for (i in 0 until subnetCount) {
            val num = i * IpUtil.getHostCount(hostBit)
            val subnet = IpUtil.addBinaryNumber(binary, num.toString(2))
            subnets.add(IpAddress(IpUtil.fromBinary(subnet), (32 - hostBit), hostBit))
        }
        return subnets
    }

    fun isValid(): Boolean {
        if ((netBit + hostBit) != 32) return false
        val octants = address.split(".").map { it.toInt() }
        if (octants.size != 4) return false
        for (octant in octants) {
            if (octant < 0 || octant > 255) return false
        }
        return true
    }

    fun isNotValid() = !isValid()

}