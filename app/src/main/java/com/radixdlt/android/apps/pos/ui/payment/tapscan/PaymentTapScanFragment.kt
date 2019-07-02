package com.radixdlt.android.apps.pos.ui.payment.tapscan

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.zxing.EncodeHintType
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.nfcread.NfcReadFragment
import com.radixdlt.android.apps.pos.nfcread.NfcReadState
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.isConnected
import com.radixdlt.android.apps.pos.util.toast
import kotlinx.android.synthetic.main.fragment_payment_tap_scan.*
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.dip

class PaymentTapScanFragment : NfcReadFragment() {

    private val args: PaymentTapScanFragmentArgs by navArgs()
    private val amount: String by lazy { args.amount ?: "0.00" }
    private val reference: String by lazy { args.reference ?: "" }
    private val paymentTapScanViewModel: PaymentTapScanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_tap_scan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initialiseViewModel()
        setCancelButtonOnClickListener()
        showQrCode()
    }

    override fun getViewModel(): NfcAdapter.ReaderCallback = paymentTapScanViewModel

    private fun initialiseViewModel() {
        paymentTapScanViewModel.setNfcToListeningState()
        paymentTapScanViewModel.paymentTapScanState.observe(viewLifecycleOwner, Observer(::navigateToComplete))
        paymentTapScanViewModel.nfcReadState.observe(viewLifecycleOwner, Observer(::onNfcCardRead))

        if (isConnected()) {
            paymentTapScanViewModel.monitorBalance(amount)
        }
    }

    private fun setCancelButtonOnClickListener() {
        cancelButton.setOnClickListener {
            val action = PaymentTapScanFragmentDirections
                .actionNavigationPaymentTapScanToNavigationPaymentCancelled()
            findNavController().navigate(action)
        }
    }

    private fun showQrCode() {
        val size = ctx.dip(resources.getDimension(R.dimen.image_view_dimen))
        val qrCode = QRCode.from(VaultPreferences.getVaultPaymentAddress())
                .withSize(size, size)
                .withHint(EncodeHintType.MARGIN, 1)
                .bitmap()

        Glide.with(ctx)
            .load(qrCode)
            .into(paymentTapScanPoweredByRadixImageView)
    }

    private fun navigateToComplete(paymentTapScanState: PaymentTapScanState) {
        when (paymentTapScanState) {
            PaymentTapScanState.RECEIVED -> {
                val action = PaymentTapScanFragmentDirections
                    .actionNavigationPaymentTapScanToNavigationPaymentComplete(amount)
                findNavController().navigate(action)
            }
            PaymentTapScanState.LISTENING -> { }
        }
    }

    private fun onNfcCardRead(nfcReadState: NfcReadState) {
        when (nfcReadState) {
            is NfcReadState.Received -> {
                val publicKey = nfcReadState.message
                if (!isConnected()) {
                    toast(getString(R.string.radix_pos_common_no_network_connection_tap_again_toast))
                    return
                }
                val action = PaymentTapScanFragmentDirections
                    .actionNavigationPaymentTapScanToNavigationPaymentPin(amount, reference, publicKey)
                findNavController().navigate(action)
            }
            NfcReadState.NfcTagLost -> toast(getString(R.string.fragment_payment_tap_scan_try_again_toast))
        }
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
        paymentTapScanViewModel.clearDisposable()
    }
}
