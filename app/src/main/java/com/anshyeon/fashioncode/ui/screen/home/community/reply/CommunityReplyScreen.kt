package com.anshyeon.fashioncode.ui.screen.home.community.reply

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.anshyeon.fashioncode.R
import com.anshyeon.fashioncode.data.model.Comment
import com.anshyeon.fashioncode.data.model.Reply
import com.anshyeon.fashioncode.data.model.User
import com.anshyeon.fashioncode.ui.component.appBar.BackButtonAppBar
import com.anshyeon.fashioncode.ui.component.commentSubmit.CommentSubmit
import com.anshyeon.fashioncode.ui.component.loadingView.LoadingView
import com.anshyeon.fashioncode.ui.component.snackBar.TextSnackBarContainer
import com.anshyeon.fashioncode.ui.theme.DarkGray
import com.anshyeon.fashioncode.util.DateFormatText

@Composable
fun CommunityReplyScreen(navController: NavHostController, comment: Comment) {

    val viewModel: CommunityReplyViewModel = hiltViewModel()

    viewModel.getUser(comment.writer)
    viewModel.getReplyList(comment.commentId)

    val scrollState = rememberScrollState()

    val replyBodyState by viewModel.replyBody.collectAsStateWithLifecycle()
    val replyListState by viewModel.replyList.collectAsStateWithLifecycle()
    val userState by viewModel.user.collectAsStateWithLifecycle()
    val isCreateReplyLoadingState by viewModel.isCreateReplyLoading.collectAsStateWithLifecycle()
    val isGetUserLoadingState by viewModel.isGetUserLoading.collectAsStateWithLifecycle()
    val isGetReplyLoadingState by viewModel.isGetReplyLoading.collectAsStateWithLifecycle()
    val isGetUserCompleteState by viewModel.isGetUserComplete.collectAsStateWithLifecycle()
    val isGetReplyCompleteState by viewModel.isGetReplyComplete.collectAsStateWithLifecycle()
    val snackBarTextState by viewModel.snackBarText.collectAsStateWithLifecycle()
    val showSnackBarState by viewModel.showSnackBar.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BackButtonAppBar {
                viewModel.navigateBack(navController)
            }
        },
        bottomBar = {
            CommentSubmit(replyBodyState, { viewModel.changeReplyBody(it) }) {
                viewModel.createReply(comment.commentId)
            }
        }
    ) {
        if (isGetUserCompleteState && isGetReplyCompleteState) {
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
                    ReplyComment(comment, userState)
                    replyListState.forEach { reply ->
                        Reply(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 45.dp, top = 10.dp),
                            reply
                        )
                    }
                }
            }
        }
        LoadingView(
            isLoading = isCreateReplyLoadingState || isGetReplyLoadingState || isGetUserLoadingState
        )
    }
}

@Composable
private fun ReplyComment(comment: Comment, user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        if (user?.profileUrl == null) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                model = user.profileUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                text = comment.nickName,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = comment.body,
            )
            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = DateFormatText.getDefaultDatePattern(comment.createdDate),
                color = DarkGray,
                fontSize = 11.sp
            )

        }
    }
}

@Composable
fun Reply(modifier: Modifier, reply: Reply) {
    Row(
        modifier = modifier
    ) {
        if (reply.profileImageUrl == null) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = null
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                model = reply.profileImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                text = reply.nickName,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = reply.body,
            )
            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = DateFormatText.getDefaultDatePattern(reply.createdDate),
                color = DarkGray,
                fontSize = 11.sp
            )
        }
    }
}