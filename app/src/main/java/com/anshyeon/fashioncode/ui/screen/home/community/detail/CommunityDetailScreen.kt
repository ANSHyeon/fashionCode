package com.anshyeon.fashioncode.ui.screen.home.community.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Post
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.commentSubmit.CommentSubmit
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.screen.home.community.reply.Reply
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.ui.theme.Gray
import com.anshyeon.fashioncode.util.DateFormatText
import com.anshyeon.fashioncode.util.DateFormatText.getDefaultDatePattern

@Composable
fun CommunityDetailScreen(navController: NavHostController, postId: String) {

    val viewModel: CommunityDetailViewModel = hiltViewModel()
    viewModel.setPostId(postId)

    val scrollState = rememberScrollState()

    val postState by viewModel.post.collectAsStateWithLifecycle()
    val commentBodyState by viewModel.commentBody.collectAsStateWithLifecycle()
    val commentListState by viewModel.commentList.collectAsStateWithLifecycle()
    val addedCommentListState by viewModel.addedCommentList.collectAsStateWithLifecycle()
    val isCreateCommentLoadingState by viewModel.isCreateCommentLoading.collectAsStateWithLifecycle()
    val isGetPostLoadingState by viewModel.isGetPostLoading.collectAsStateWithLifecycle()
    val isGetCommentListLoadingState by viewModel.isGetCommentListLoading.collectAsStateWithLifecycle()
    val isGetPostCompleteState by viewModel.isGetPostComplete.collectAsStateWithLifecycle()
    val isGetCommentListCompleteState by viewModel.isGetCommentListComplete.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()


    Scaffold(
        topBar = {
            BackButtonAppBar(stringResource(id = R.string.label_app_bar_community_detail)) {
                viewModel.navigateBack(navController)
            }
        },
        bottomBar = {
            CommentSubmit(commentBodyState, { viewModel.changeCommentBody(it) }) {
                viewModel.createComment(postId)
            }
        }
    ) {
        TextSnackBarContainer(
            snackbarText = snackBarTextState,
            showSnackbar = showSnackBarState,
            onDismissSnackbar = { viewModel.dismissSnackBar() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(15.dp)
                    .verticalScroll(scrollState)
            ) {
                if (isGetPostCompleteState && isGetCommentListCompleteState) {
                    DetailContent(postState) {
                        viewModel.navigateOtherUserProfile(navController, postState?.writer)
                    }
                    if (commentListState.isNotEmpty() || addedCommentListState.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(10.dp))
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(Gray)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = stringResource(id = R.string.label_comment),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                        commentListState.forEach { comment ->
                            Comment(
                                comment,
                                {
                                    viewModel.navigateOtherUserProfile(
                                        navController,
                                        it
                                    )
                                }) {
                                viewModel.navigateCommunityReply(navController, it)
                            }
                        }
                        addedCommentListState.forEach { comment ->
                            Comment(
                                comment,
                                {
                                    viewModel.navigateOtherUserProfile(
                                        navController,
                                        it
                                    )
                                }) {
                                viewModel.navigateCommunityReply(navController, it)
                            }
                        }
                    }
                }
            }
            LoadingView(
                isLoading = isCreateCommentLoadingState || isGetPostLoadingState || isGetCommentListLoadingState
            )

        }
    }
}

@Composable
fun DetailContent(post: Post?, onNavigateOtherProfile: () -> Unit
) {
    UserProfileDefault(post?.createdDate, post?.writerNickName, post?.writerProfileImageUrl) {
        onNavigateOtherProfile()
    }
    Spacer(modifier = Modifier.size(20.dp))
    Text(
        text = post?.title ?: "제목을 불러올 수 없습니다.",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.size(10.dp))
    Text(
        text = post?.body ?: "내용을 불러올 수 없습니다.",
        fontSize = 14.sp
    )
    Spacer(modifier = Modifier.size(15.dp))
    ImageList(post)
}

@Composable
fun UserProfileDefault(
    createDate: String?,
    userNickName: String?,
    userProfileUrl: String?,
    onNavigateOtherProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (userProfileUrl == null) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigateOtherProfile() },
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateOtherProfile() },
                model = userProfileUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                modifier = Modifier
                    .clickable { onNavigateOtherProfile() },
                text = userNickName ?: "닉네임을 불러올 수 없습니다.",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = DateFormatText.getElapsedTime(createDate),
                color = DarkGray
            )
        }
    }
}

@Composable
private fun ImageList(post: Post?) {
    post?.imageUrlList?.forEach { url ->
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_place_holder)
        )
    }
}

@Composable
private fun Comment(
    comment: Comment,
    onNavigateOtherProfile: (String) -> Unit,
    onclick: (comment: Comment) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        if (comment.profileImageUrl == null) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigateOtherProfile(comment.writer) },
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNavigateOtherProfile(comment.writer) },
                model = comment.profileImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                modifier = Modifier.clickable { onNavigateOtherProfile(comment.writer) },
                text = comment.nickName,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = comment.body,
            )
            Spacer(modifier = Modifier.size(5.dp))
            Row(
                modifier = Modifier.clickable {
                    onclick(comment)
                }
            ) {
                Text(
                    text = getDefaultDatePattern(comment.createdDate),
                    color = DarkGray,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = stringResource(id = R.string.label_reply),
                    color = DarkGray,
                    fontSize = 11.sp
                )
            }
            comment.replyList?.let { replyList ->
                replyList.take(4).forEach { reply ->
                    Reply(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        reply
                    ) {
                        onNavigateOtherProfile(reply.writer)
                    }
                }
                if (replyList.size > 4) {
                    Text(
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                onclick(comment)
                            },
                        text = "대댓글 ${replyList.size - 4}개 더 보기 >",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}