package com.example.qldrl.FCM;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


public class AccessToken {
    private static final String firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging";
    public String getAccessToken(){
        try{
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"miniapptest-705a9\",\n" +
                    "  \"private_key_id\": \"bc564742807392c92487bbc30ce09663338afeb5\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCUJ/s1/2a60mZs\\nMUFa/Vt9AfBX8B3XHQHNFJzdEAalSBQdfm1+IwVc4WpXhgvEd15U4J95xm5+wCkd\\nYlhWOQnDK83IZlzgxQ3LB9hCkPrzMlDNade0CBPgcBagB43BCxJkVqcv5foOSiOy\\n9nRqBVrh6Sd+OEXjH3U5QE8+4RF75EyNdbS26i8PvS9kIPH5Bd4nF9fIUefmWN0U\\ndYeHMta1fCBEb7Xfj2yMCIyKJAWOf6KJglL+FvdLBSaS4D1p6EgpLRy5PZdEtALl\\nPnTcdhWkcg7S0RP95MjPDvJvNw4ao+b/UL9zYr48Jl4qRWQWha+V+TMtqepgDmZL\\n4YDfNANpAgMBAAECggEACRtmDT0sVuqNAjSK5GlzgL0oRXeMlgcB79XHCK2uAuXp\\n2qBFYb4MW9e9XjDusz51LXl7RBSHAUKVpGlBrdcmuvkL1rg4GFb0kJjkWb8otd9P\\naTkK2dWvRxoMwfyE4atpD4LmFwMnek5jYGJW6Jd4rS9UTqVt4layMXk7Bc328asQ\\necuwdVWOfSjS1gV30UMAkU7azjvGuKMglmBZboXspM5j7NM1yrm+o9SSLZtBCkjZ\\nK5AxGOrDbx6z5j/FgNVVbekV8ib5QCoFJotkgCXnvhuzy32tG/rRoFLX/TKRjVtZ\\nitcbhPxGdo4nF+JpkCHaSyCNPuzjuooiT401dukACQKBgQDFKLnyG3o/SGvgDfvY\\nC/FbvR96vhSOmtOoMo4bYTn5/MQZYBEI/xh2KPnlH6luIyrsOimCupSyTdwh5rOj\\nCfLSMBwWwUEsL7FwSCxMtFIJxgxVWMDuoLUIxpiZiGD9RiIrrjSrVvgHgIZ1ZgFj\\nyx6uqIOR4KlZpwMM4ds6wnphPQKBgQDAX1pr5T8UfsIzq+oTfkPlDSHyqAWvL0Be\\n4w1G6vIluQCtsJodxcXFAYfWhmFivHS+KeUFMQ6nnyInZ3gEffamyx7zBDlVoEnS\\nKBwJHISEm4yW/XsGqSlroh8Tull3LXqnEKGp6u02F6I4IoF8hsrnktG31Gmpo0nD\\nHPjlauP1nQKBgQC8T73q5RvKPd3rTu/w1tddDDJ5BfyUiIUI7eCVfjPl9etHYV5j\\n/WLb2R92HNuzepuQrazgVF2aQIu1RajmXKY05AawJT2eep8743OMf96rvyIgknkU\\ncyL2ktlGK+CXiNkyZ12fGZ4lbDkBAMDrFCLmJ8YjGzYlj0N2lnLSmNZS8QKBgGsM\\nZK/HyPtpsj24u7APQk7SqQJ0F32tyJ9Er9233fm54jPFI23P4UbInEN3Uff0sO4l\\nOAlUjDggqIvO2w183uVgAJ/wyMrqQEoPAdLcKK32NRzMf0psld0MQUjkGOblyuvu\\nqFd6oHfKXP3pza5y/7Qvkj4+mHHKqMBHfgWEHshNAoGACD/HeI54b2PaXRsCAJQm\\nQimqUvoikJoBlfcKoe5SGRTWj1Ya1CKa5cdsg19bIVs3a21gtOhFBOJEZFG8yUl3\\niXBlh6NbhtL4vX5q6qECOkBVUTgJwOHMe9EtFsu0l5VonGkglBirEIeT8Dz5lXsb\\nho2Em4eR3B0LFq06Y2yWApU=\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-1tf16@miniapptest-705a9.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"116675890741199961468\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-1tf16%40miniapptest-705a9.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(Collections.singletonList(firebaseMessagingScope));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
