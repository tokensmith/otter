package helper.entity.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import net.tokensmith.otter.translatable.Translatable;


import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;

public class DummyPayload implements Translatable {
    @NotNull
    private Integer integer;

    private String string;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate localDate;
    private Optional<Integer> integerOptional;

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Optional<Integer> getIntegerOptional() {
        return integerOptional;
    }

    public void setIntegerOptional(Optional<Integer> integerOptional) {
        this.integerOptional = integerOptional;
    }
}
