package com.tweetapp.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

	@KafkaListener(topics = "PasswordChangeTopic", groupId = "twitter-group")
	public void listenToCodeDecodeKafkaTopic(String messageReceived) {
		System.out.println("Message received, The user trying to change password:" + messageReceived);
	}
}
