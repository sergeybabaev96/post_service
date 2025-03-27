package faang.school.postservice.exception.album;

import lombok.Getter;

@Getter
public class AlbumException extends RuntimeException {

    public static final String ALBUM_NOT_FOUND = "Album not found";
    public static final int ALBUM_NOT_FOUND_CODE = 800;

    private int exceptionCode;

    public AlbumException(String message, int exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }
}
