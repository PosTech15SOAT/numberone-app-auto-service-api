package br.com.fiap.numberone.serviceorder.domain.references;

import br.com.fiap.numberone.customer.domain.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private UUID id;
    private String name;
    private TipoDocumento documentType;
    private String document;
    private String email;
    private String phone;
    private String address;
    private Boolean active;
}


