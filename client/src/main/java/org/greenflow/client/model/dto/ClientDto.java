package org.greenflow.client.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.greenflow.client.model.entity.Client;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientDto {

    String email;

    String firsName;

    String lastName;

    String phone;

    String city;

    public static ClientDto fromEntity(Client client) {
        return ClientDto.builder()
                .email(client.getEmail())
                .firsName(client.getFirsName())
                .lastName(client.getLastName())
                .phone(client.getPhone())
                .city(client.getCity())
                .build();
    }
}
