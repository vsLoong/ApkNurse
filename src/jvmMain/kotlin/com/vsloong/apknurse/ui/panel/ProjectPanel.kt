package com.vsloong.apknurse.ui.panel

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vsloong.apknurse.bean.FileItemInfo
import com.vsloong.apknurse.bean.ProjectTreeType
import com.vsloong.apknurse.bean.action.ProjectPanelAction
import com.vsloong.apknurse.bean.state.ProjectPanelState
import com.vsloong.apknurse.manager.NurseManager
import com.vsloong.apknurse.ui.scroll.ScrollPanel
import com.vsloong.apknurse.ui.theme.appBarColor
import com.vsloong.apknurse.ui.theme.textColor
import com.vsloong.apknurse.utils.getResByFileItem

/**
 * Project 面板相关UI
 */

@Composable
fun ProjectPanel(
    modifier: Modifier = Modifier,
    projectPanelAction: ProjectPanelAction = NurseManager.projectPanelViewModel.projectPanelAction,
    projectPanelState: ProjectPanelState = NurseManager.projectPanelViewModel.projectPanelState.value
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
                .background(color = appBarColor)
                .clickable { projectPanelAction.onProjectTreeTypeClick(projectPanelState.projectTreeType) }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = when (projectPanelState.projectTreeType) {
                    is ProjectTreeType.PROJECT -> {
                        (projectPanelState.projectTreeType as ProjectTreeType.PROJECT).name
                    }

                    is ProjectTreeType.PACKAGES -> {
                        (projectPanelState.projectTreeType as ProjectTreeType.PACKAGES).name
                    }
                },
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )

            Image(
                painter = painterResource(resourcePath = "icons/arrow_right.svg"),
                contentDescription = "",
                modifier = Modifier.size(10.dp)
                    .rotate(90f)
            )
        }


        val horizontalScrollState = rememberScrollState()
        val verticalScrollState = rememberLazyListState()


        // 工程树结构
        ScrollPanel(
            modifier = Modifier.weight(1f)
                .background(color = appBarColor)
                .padding(2.dp),
            horizontalScrollStateAdapter = rememberScrollbarAdapter(horizontalScrollState),
            verticalScrollStateAdapter = rememberScrollbarAdapter(verticalScrollState)
        ) {
            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(4.dp), // 设置后会导致VerticalScrollbar的显示异常
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState),
                state = verticalScrollState,
            ) {
                itemsIndexed(
                    items = projectPanelState.showedTreeList,
                    key = { index, item ->
                        item.parent + item.name
                    }
                ) { index, item ->
                    ProjectItem(
                        item = item,
                        onClick = {
                            projectPanelAction.onFileItemClick(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectItem(
    item: FileItemInfo,
    onClick: (FileItemInfo) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .height(24.dp)
            .widthIn(min = 300.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick(item) }
    ) {

        Spacer(modifier = Modifier.width((item.depth * 16).dp))

        // 文件夹才有箭头选项
        if (item.isDir) {
            Image(
                painter = painterResource(resourcePath = "icons/arrow_right.svg"),
                contentDescription = "",
                modifier = Modifier.size(10.dp)
                    .rotate(
                        if (item.selected) {
                            90f
                        } else {
                            0f
                        }
                    )
            )
        }

        Image(
            painter = painterResource(resourcePath = getResByFileItem(item)),
            contentDescription = "",
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = item.showName.ifEmpty {
                item.name
            },
            color = textColor,
            fontSize = if (item.isRootFile) {
                16.sp
            } else {
                14.sp
            },
            fontWeight = if (item.isRootFile) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
        )
    }
}