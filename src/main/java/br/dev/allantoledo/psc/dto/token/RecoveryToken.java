package br.dev.allantoledo.psc.dto.token;

import lombok.Data;

@Data
public class RecoveryToken {
    private String link;
    private String attention = "Isto é apenas para demonstração, em " +
            "um caso real o link seria enviado diretamente ao email.";
}
