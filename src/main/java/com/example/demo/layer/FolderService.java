package com.example.demo.layer;

import static com.example.demo.common.ErrorMessage.SERIES_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.FOLDER_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.layer.entity.Folder;
import com.example.demo.layer.entity.Series;
import com.example.demo.layer.repository.FolderRepository;
import com.example.demo.layer.repository.SeriesRepository;
import com.example.demo.layer.request.CreateFolderRequest;
import com.example.demo.layer.request.UpdateFolderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final SeriesRepository seriesRepository;

    @Transactional
    public Folder createFolder(CreateFolderRequest request) {

        Series series = seriesRepository.findById(request.getSeriesId()).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));

        if (series.isDeleted()) {
            throw new ApplicationException(SERIES_NOT_FOUND);
        }

        return folderRepository.save(Folder.createFolder(request));
    }

    @Transactional
    public Folder updateFolder(UpdateFolderRequest request) {

        Series series = seriesRepository.findById(request.getSeriesId()).orElseThrow(() -> new ApplicationException(SERIES_NOT_FOUND));

        if (series.isDeleted()) {
            throw new ApplicationException(SERIES_NOT_FOUND);
        }

        Folder folder = folderRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(FOLDER_NOT_FOUND));

        if (folder.isDeleted()) {
            throw new ApplicationException(FOLDER_NOT_FOUND);
        }

        folder.update(request);

        return folderRepository.save(folder);
    }

    @Transactional
    public void deleteFolder(long folderId) {
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new ApplicationException(FOLDER_NOT_FOUND));

        if (folder.isDeleted()) {
            throw new ApplicationException(FOLDER_NOT_FOUND);
        }

        folder.delete();
        folderRepository.save(folder);
    }
}
