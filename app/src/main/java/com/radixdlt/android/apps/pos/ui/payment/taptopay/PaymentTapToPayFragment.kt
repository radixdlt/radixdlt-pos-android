package com.radixdlt.android.apps.pos.ui.payment.taptopay

import android.animation.Animator
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieDrawable
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.nfcread.NfcReadFragment
import com.radixdlt.android.apps.pos.nfcread.NfcReadState
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentViewModel
import com.radixdlt.android.apps.pos.util.CardPinError
import com.radixdlt.android.apps.pos.util.EmptyAnimatorListener
import com.radixdlt.android.apps.pos.util.PIN_ERROR_FIRST_ATTEMPT
import com.radixdlt.android.apps.pos.util.PIN_ERROR_SECOND_ATTEMPT
import com.radixdlt.android.apps.pos.util.isConnected
import com.radixdlt.android.apps.pos.util.toast
import kotlinx.android.synthetic.main.fragment_payment_tap_to_pay.*

class PaymentTapToPayFragment : NfcReadFragment() {

    private val args: PaymentTapToPayFragmentArgs by navArgs()
    private val amount: String by lazy { args.amount ?: "0.00" }
    private val reference: String by lazy { args.reference ?: "" }
    private val pin: String by lazy { args.pin ?: "" }
    private val publicKey: String by lazy { args.publicKey ?: "" }

    private val paymentViewModel: PaymentViewModel by activityViewModels()

    private val unsignedAtom by lazy { paymentViewModel.unsignedAtom }

    private val paymentTapToPayViewModel: PaymentTapToPayViewModel by viewModels(
        factoryProducer = { PaymentTapToPayViewModelFactory(unsignedAtom, pin, publicKey) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_tap_to_pay, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initialiseViewModel()
    }

    private fun initialiseViewModel() {
        paymentTapToPayViewModel.cardErrorState.observe(viewLifecycleOwner, Observer(::onCardErrorReceived))
        paymentTapToPayViewModel.nfcReadState.observe(viewLifecycleOwner, Observer(::onNfcTagErrorReceived))
        paymentTapToPayViewModel.paymentTapToPaySubmitAtomState.observe(
            viewLifecycleOwner, Observer(::onPaymentSubmitAtomState)
        )
    }

    private fun onNfcTagErrorReceived(nfcReadState: NfcReadState) {
        when (nfcReadState) {
            NfcReadState.NfcTagLost -> showErrorMessageMessage(getString(R.string.fragment_payment_tap_to_pay_tapped_too_quick_try_again_message))
        }
    }

    private fun onCardErrorReceived(cardErrorState: PaymentTapToPayCardErrorState) {
        when (cardErrorState) {
            is PaymentTapToPayCardErrorState.PinError -> setCardPinErrorMessage(cardErrorState.code)
            PaymentTapToPayCardErrorState.Blocked -> navigateToCardError()
        }
    }

    private fun onPaymentSubmitAtomState(paymentTapToPaySubmitAtomState: PaymentTapToPaySubmitAtomState) {
        when (paymentTapToPaySubmitAtomState) {
            PaymentTapToPaySubmitAtomState.INITIAL -> {}
            PaymentTapToPaySubmitAtomState.READY -> atomSubmissionPreparation()
            PaymentTapToPaySubmitAtomState.LOADING -> atomSubmitting()
            PaymentTapToPaySubmitAtomState.SUBMITTED -> atomSubmitted()
            PaymentTapToPaySubmitAtomState.ERROR -> atomSubmissionError()
        }
    }

    private fun atomSubmissionPreparation() {
        if (!isConnected()) {
            toast(getString(R.string.radix_pos_common_no_network_connection_tap_again_toast))
            return
        }
        paymentTapToPayViewModel.submitAtom()
    }

    private fun showErrorMessageMessage(message: String) {
        errorMessageTextView.text = message
        errorMessageTextView.visibility = View.VISIBLE
    }

    private fun navigateToCardError() {
        val action = PaymentTapToPayFragmentDirections
            .actionNavigationPaymentTapToPayToNavigationPaymentCardError(amount, reference, true)
        findNavController().navigate(action)
    }

    private fun atomSubmitting() {
        if (errorMessageTextView.visibility == View.VISIBLE) {
            errorMessageTextView.visibility = View.GONE
        }

        detectingButton.backgroundTintList = setButtonColor(R.color.colorAccent)
        detectingButton.text = getString(R.string.fragment_payment_tap_to_pay_processing_button)
        nfcImageView.visibility = View.GONE
        loadingParticlesRocketAnimationView.visibility = View.VISIBLE
        loadingParticlesRocketAnimationView.playAnimation()

        loadingParticlesRocketAnimationView
            .addAnimatorListener(object : Animator.AnimatorListener by EmptyAnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    loadingParticlesRocketAnimationView.setMinFrame(79)
                    loadingParticlesRocketAnimationView.playAnimation()
                    loadingParticlesRocketAnimationView.repeatMode = LottieDrawable.RESTART
                }
            })
    }

    private fun atomSubmissionError() {
        paymentTapToPayViewModel.atomSubmissionStateInitial()
        detectingButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
        detectingButton.text = getString(R.string.radix_pos_common_detecting_button_xml)
        nfcImageView.visibility = View.VISIBLE
        loadingParticlesRocketAnimationView.visibility = View.GONE
        loadingParticlesRocketAnimationView.cancelAnimation()
        showErrorMessageMessage(getString(R.string.fragment_payment_tap_to_pay_error_submitting_try_again_message))
    }

    private fun atomSubmitted() {
        paymentTapToPayViewModel.atomSubmissionStateInitial()
        loadingParticlesRocketAnimationView.pauseAnimation()
        navigateToPaymentComplete()
    }

    private fun setCardPinErrorMessage(@CardPinError code: Int) {
        when (code) {
            PIN_ERROR_FIRST_ATTEMPT -> paymentViewModel.incorrectPin =
                getString(R.string.fragment_payment_pin_two_attempts_remaining_error)
            PIN_ERROR_SECOND_ATTEMPT -> paymentViewModel.incorrectPin =
                getString(R.string.fragment_payment_pin_one_attempt_remaining_error)
        }
        findNavController().navigateUp()
    }

    override fun getViewModel(): NfcAdapter.ReaderCallback = paymentTapToPayViewModel

    private fun navigateToPaymentComplete() {
        val action = PaymentTapToPayFragmentDirections
            .actionNavigationPaymentTapToPayToNavigationPaymentComplete(amount)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_payment_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_quit_payment -> {
                findNavController().navigate(R.id.navigation_payment_cancel)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        paymentTapToPayViewModel.clearDisposable()
    }
}
