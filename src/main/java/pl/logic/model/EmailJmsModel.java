package pl.logic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class EmailJmsModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String awsFileName;
    private String awsTestFileName;
}
