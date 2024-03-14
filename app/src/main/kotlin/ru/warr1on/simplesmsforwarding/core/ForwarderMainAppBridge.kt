package ru.warr1on.simplesmsforwarding.core

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.warr1on.simplesmsforwarding.domain.services.messageForwarding.MessageForwardingService

/**
 * A bridge that allows communication between the forwarder and the main app
 */
class ForwarderMainAppBridge : KoinComponent {

    val messageForwardingService: MessageForwardingService by inject()
}
