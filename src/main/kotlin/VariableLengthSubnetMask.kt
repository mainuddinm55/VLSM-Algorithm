import java.util.*

class VariableLengthSubnetMask(private val strAddress: String, private val lanSize: List<Int>) {
    private val networkAddress: IpAddress
    private val networkSize: Stack<Int> = Stack()
    private val addressesStack: Stack<IpAddress> = Stack()
    private val subnetAddresses = mutableListOf<IpAddress>()
    val remaining: List<IpAddress>
        get() = addressesStack.toList()
    val subnets: List<IpAddress>
        get() = subnetAddresses

    init {
        networkAddress = parseIPAddress()
        networkSize.addAll(lanSize.sorted())
        addressesStack.push(networkAddress)
    }

    private fun canSubnetting(): Boolean {
        var needAddress = 0
        for (n in lanSize) {
            needAddress += IpUtil.closestHost(n)
        }
        val availableAddress = networkAddress.availableHosts()
        return needAddress <= availableAddress
    }

    fun subnet() {
        if (networkAddress.isNotValid()) throw Exception("Invalid given network address")
        if (!canSubnetting()) throw Exception("Subnet can not possible from his network")
        while (networkSize.isNotEmpty() && addressesStack.isNotEmpty()) {
            val hosts = networkSize.pop()
            val address = addressesStack.pop()
            if (address.isDivisible(hosts)) {
                val subnets = address.divideSubAddresses(hosts)
                subnetAddresses.add(subnets[0].copy())
                for (i in 1 until subnets.size) {
                    addressesStack.push(subnets[i])
                }
            } else {
                subnetAddresses.add(address)
            }
        }
    }

    private fun parseIPAddress(): IpAddress {
        val slashIndex = strAddress.indexOf("/")
        if (slashIndex == -1) {
            throw Exception("Network bit not found")
        }
        val addressOnly = strAddress.substring(0, slashIndex)
        val networkBit =
            strAddress.substring((slashIndex + 1)).toIntOrNull() ?: throw Exception("Network bit not found")
        if (networkBit < 0 || networkBit > 32) {
            throw Exception("Invalid ip address")
        }
        return IpAddress(addressOnly, networkBit, (32 - networkBit))
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val lans = mutableListOf<Int>()
    println("How many network do you want to create?")
    val numOfNetwork = scanner.nextInt()
    for (i in 1..numOfNetwork) {
        println("How many users for network # $i")
        lans.add(scanner.nextInt())
    }
    println("Give your network address format: (x.x.x.x/x)")
    scanner.nextLine()
    val network = scanner.nextLine()
    scanner.close()
    val variableLengthSubnetMask = VariableLengthSubnetMask(network, lans)
    try {
        variableLengthSubnetMask.subnet()
        printHeading()
        for (address in variableLengthSubnetMask.subnets) {
            println("${address.address} \t \t | ${address.getFirstAddress()} \t | ${address.getLastAddress()} \t | ${address.geSubnetMask()} \t | ${address.assignableHosts()}")
        }
        printHeading()
        for (address in variableLengthSubnetMask.remaining) {
            println("${address.address} \t | ${address.getFirstAddress()}\t | ${address.getLastAddress()}\t | ${address.geSubnetMask()}\t | ${address.assignableHosts()}")
        }

    } catch (e: Exception) {
        println(e.message)
    }
}

private fun printHeading() {
    println()
    println("Network Address \t | First Address \t | Last Address \t | Subnet Mask \t | Assignable Host")
    println("---------------------------------------------------------------------------------------------")
}