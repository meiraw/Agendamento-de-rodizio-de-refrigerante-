package com.lacoca.lacoquinha.DTO.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class PessoasRequestDTO {

    @NotBlank(message = "O nome não pode ser vazio!")
    @Size(min = 5 , max = 50 , message = "O nome tem que ter de 5 à 50 caracteres; " )
    private String nome;

    @NotBlank(message = "O apelido não pode ser vazio!")
    @Size(min = 4 , max = 50 , message = "O apelido tem que ter de 5 à 50 caracteres; " )
    private String apelido;
}
