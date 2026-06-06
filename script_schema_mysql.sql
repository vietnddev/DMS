DROP TABLE document_tree_view;

CREATE OR REPLACE VIEW document_tree_view AS
WITH RECURSIVE DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, Path, HierarchyLevel) AS (
	SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, CAST(NAME AS CHAR(4000)) AS Path, 0 AS HierarchyLevel
	FROM document
	WHERE PARENT_ID = 0
    -- AND IS_FOLDER = 'Y'
	UNION ALL
	SELECT d.ID, d.NAME, d.AS_NAME, d.PARENT_ID, d.IS_FOLDER, CONCAT(dh.Path, '/', d.NAME, ''), dh.HierarchyLevel + 1
	FROM document d
	INNER JOIN DocumentHierarchy dh ON d.PARENT_ID = dh.ID
	-- WHERE d.IS_FOLDER = 'Y'
),
RecursiveHierarchy AS (
	SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, HierarchyLevel, Path,
	ROW_NUMBER() OVER (PARTITION BY SUBSTRING_INDEX(Path, '/', -1) ORDER BY Path) AS RowNumm
	FROM DocumentHierarchy
),
SubFolderList AS (
 	SELECT dh.PARENT_ID AS Parent_ID,
	GROUP_CONCAT(dh.ID ORDER BY dh.ID SEPARATOR '|') AS SubFoldersId
	FROM DocumentHierarchy dh
    WHERE dh.IS_FOLDER = 'Y'
	GROUP BY dh.PARENT_ID
),
SubFileList AS (
 	SELECT dh.PARENT_ID AS Parent_ID,
	GROUP_CONCAT(dh.ID ORDER BY dh.ID SEPARATOR '|') AS SubFilesId
	FROM DocumentHierarchy dh
    WHERE dh.IS_FOLDER = 'N'
	GROUP BY dh.PARENT_ID
)
SELECT
    rh.ID AS IDTemp,
    rh.NAME AS NameTemp,
    rh.AS_NAME AS AsNameTemp,
    rh.PARENT_ID AS ParentIdTemp,
    CASE WHEN EXISTS (SELECT 1 FROM document sub WHERE sub.PARENT_ID = rh.ID AND sub.IS_FOLDER = 'Y') THEN 'Y' ELSE 'N' END AS Has_SubFolders,
    CASE WHEN EXISTS (SELECT 1 FROM document sub WHERE sub.PARENT_ID = rh.ID AND sub.IS_FOLDER = 'N') THEN 'Y' ELSE 'N' END AS Has_SubFiles,
    sfo.SubFoldersId,
    sfi.SubFilesId,
    rh.HierarchyLevel,
    rh.RowNumm,
    RTRIM(rh.Path) AS PATH,
    d.*
FROM RecursiveHierarchy rh
         LEFT JOIN SubFolderList sfo ON rh.ID = sfo.Parent_ID
         LEFT JOIN SubFileList sfi ON rh.ID = sfi.Parent_ID
         INNER JOIN document d ON d.ID = rh.ID;

-- No line after this line